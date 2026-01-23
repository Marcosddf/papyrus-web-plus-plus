/*****************************************************************************
 * Copyright (c) 2025 CEA LIST.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Pascal Bannerot (CEA LIST) <pascal.bannerot@cea.fr> - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.web.application.document.controllers;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.application.document.services.api.IDocumentDownloadResourceSearchService;
import org.eclipse.sirius.web.application.document.services.api.IDocumentExporter;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.ProfileApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Declare an endpoint for downloading document with embedded references. Download an UML model and its profile and
 * importedPackage references, produce a multipart/form-data file
 *
 * <pre>
 * PROTOCOL://DOMAIN.TLD(:PORT)/API_BASE_PATH/editingcontexts/EDITING_CONTEXT_ID/documentsWithReferences/DOCUMENT_ID
 * </pre>
 *
 * @author Pascal Bannerot (CEA LIST) <pascal.bannerot@cea.fr>
 */

@RestController
@RequestMapping("/api/editingcontexts/{editingContextId}/documentsWithReferences")
public class DocumentDownloadWithReferencesController {
    private final Logger logger = LoggerFactory.getLogger(DocumentDownloadWithReferencesController.class);

    @Autowired
    private IEditingContextSearchService editingContextSearchService;

    private final List<IDocumentExporter> documentExporters;

    private final List<IDocumentDownloadResourceSearchService> documentDownloadResourceSearchServices;

    public DocumentDownloadWithReferencesController(List<IDocumentExporter> documentExporters,
            List<IDocumentDownloadResourceSearchService> documentDownloadResourceSearchServices) {
        this.documentExporters = Objects.requireNonNull(documentExporters);
        this.documentDownloadResourceSearchServices = Objects.requireNonNull(documentDownloadResourceSearchServices);
    }

    /**
     *
     * Download the document and all dependencies (imports and applied profiles).
     *
     * @param editingContextId
     * @param documentId
     * @param requestHeaders
     * @return
     */
    @ResponseBody
    @GetMapping(path = "/{documentId}")
    public ResponseEntity<MultiValueMap<String, Object>> downloadDocumentWithReferences(@PathVariable String editingContextId, @PathVariable String documentId,
            @RequestHeader HttpHeaders requestHeaders) {
        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();

        Optional<org.eclipse.emf.ecore.resource.Resource> optionalResource = this.documentDownloadResourceSearchServices.stream()
                .map(documentDownloadResourceSearchService -> documentDownloadResourceSearchService.findResource(editingContextId, documentId))
                .filter(Optional::isPresent)
                .findFirst()
                .orElseGet(() -> this.getResource(editingContextId, documentId));

        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();

            if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof Model) {
                Model model = (Model) resource.getContents().get(0);
                this.getRecursiveReferences(model, new HashSet<>(), multipartMap, requestHeaders);
                this.addResource(multipartMap, requestHeaders, model.eResource());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            return new ResponseEntity<>(multipartMap, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Get all references recursively and update {@link multipartMap} accordingly.
     *
     * @param umlPackage
     *            the package to browse
     * @param uris
     *            already find uris (to update)
     * @param multipartMap
     *            the map to fill
     * @param requestHeaders
     *            the request header
     */
    private void getRecursiveReferences(Package umlPackage, HashSet<URI> uris, MultiValueMap<String, Object> multipartMap, HttpHeaders requestHeaders) {
        for (PackageImport pi : umlPackage.getPackageImports()) {
            if (pi.getImportedPackage() != null) {
                EObject eo = pi.getImportedPackage().eResource().getContents().get(0);
                if (eo instanceof Model m) {
                    URI modelURI = m.eResource().getURI();
                    if (uris.contains(modelURI)) {
                        return;
                    }
                    uris.add(modelURI);
                    this.logger.debug(m.getName());
                    this.addResource(multipartMap, requestHeaders, m.eResource());
                    this.getRecursiveReferences(m, uris, multipartMap, requestHeaders);
                }
            } else {
                this.logger.debug(pi.toString());
            }
        }
        for (ProfileApplication pa : umlPackage.getProfileApplications()) {
            if (pa.getAppliedProfile() != null) {
                EObject eo = pa.getAppliedProfile().eResource().getContents().get(0);
                if (eo instanceof Profile p) {
                    URI profileURI = p.eResource().getURI();
                    if (uris.contains(profileURI)) {
                        return;
                    }
                    uris.add(profileURI);

                    this.logger.debug(p.getName());
                    this.addResource(multipartMap, requestHeaders, p.eResource());
                    this.getRecursiveReferences(p, uris, multipartMap, requestHeaders);
                }
            }
        }

    }

    /**
     * Add the given {@link resource} to the {@link multipartMap}.
     *
     * @param multipartMap
     * @param requestHeaders
     * @param resource
     */
    private void addResource(MultiValueMap<String, Object> multipartMap, HttpHeaders requestHeaders, org.eclipse.emf.ecore.resource.Resource resource) {

        Optional<MediaType> optionalAcceptHeaderValue = Optional.empty();
        Optional<IDocumentExporter> optionalDocumentExporter = Optional.empty();

        Iterator<MediaType> iterator = requestHeaders.getAccept().iterator();
        while (iterator.hasNext() && optionalDocumentExporter.isEmpty()) {
            MediaType acceptHeaderValue = iterator.next();

            optionalAcceptHeaderValue = Optional.of(acceptHeaderValue);
            optionalDocumentExporter = this.documentExporters.stream()
                    .filter(documentExporter -> documentExporter.canHandle(resource, acceptHeaderValue.removeQualityValue().toString()))
                    .findFirst();
        }

        if (optionalDocumentExporter.isPresent() && optionalAcceptHeaderValue.isPresent()) {
            IDocumentExporter documentExporter = optionalDocumentExporter.get();
            MediaType acceptHeaderValue = optionalAcceptHeaderValue.get();

            Optional<byte[]> optionalContent = documentExporter.getBytes(resource, acceptHeaderValue.toString());
            if (optionalContent.isPresent()) {
                byte[] content = optionalContent.get();

                String name = resource.eAdapters().stream()
                        .filter(ResourceMetadataAdapter.class::isInstance)
                        .map(ResourceMetadataAdapter.class::cast)
                        .findFirst()
                        .map(ResourceMetadataAdapter::getName)
                        .orElse("resource");
                ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                        .filename(name)
                        .build();

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentDisposition(contentDisposition);
                responseHeaders.setContentType(acceptHeaderValue);
                responseHeaders.setContentLength(content.length);
                responseHeaders.set("uri", resource.getURI().toString().replaceFirst("sirius:///", ""));

                InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
                HttpEntity<InputStreamResource> httpEntity = new HttpEntity<>(inputStreamResource, responseHeaders);

                multipartMap.add(name, httpEntity);
            }
        }
    }

    private Optional<org.eclipse.emf.ecore.resource.Resource> getResource(String editingContextId, String documentId) {
        return this.editingContextSearchService.findById(editingContextId)
                .filter(IEMFEditingContext.class::isInstance)
                .map(IEMFEditingContext.class::cast)
                .flatMap(editingContext -> {
                    URI uri = new JSONResourceFactory().createResourceURI(documentId);
                    return editingContext.getDomain().getResourceSet().getResources().stream()
                            .filter(resource -> resource.getURI().equals(uri))
                            .findFirst();
                });
    }

}
