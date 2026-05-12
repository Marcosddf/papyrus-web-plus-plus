/*******************************************************************************
 * Copyright (c) 2022, 2024, 2026 CEA LIST, Obeo.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Obeo - Initial API and implementation
 *  Dilan EESHVARAN (CEA LIST) - Issue 323
 *******************************************************************************/
package org.eclipse.papyrus.web.application.profile.dto;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * The basic information of an UML profile.
 *
 * @author lfasani
 */
public class UMLProfileMetadata {

    private final String label;

    /**
     * The path of the UML profile file in the project.
     */
    private final String uriPath;

    /**
     * The last version of the UML profile file in the project.
     */
    private final String version;

    /** 
     * The author recorded at last publish time, or empty string if not set. 
     */
    private final String author;

    /**
     * The date recorded at last publish time (ISO format), or empty string if not set. 
     */
    private final String date;

    /**
     * The comment recorded at last publish time, or empty string if not set.
     */
    private final String comment;

    /**
     * The copyright recorded at last publish time, or empty string if not set. */
    private final String copyright;

    /**
     * Full constructor used for dynamic (published) profiles.
     */
    public UMLProfileMetadata(String label, String uriPath, String version, String author, String date, String comment, String copyright) {
        this.label = Objects.requireNonNull(label);
        this.uriPath = Objects.requireNonNull(uriPath);
        this.version = Objects.requireNonNull(version);
        this.author = Objects.requireNonNull(author);
        this.date = Objects.requireNonNull(date);
        this.comment = Objects.requireNonNull(comment);
        this.copyright = Objects.requireNonNull(copyright);
    }

    /**
     * constructor for static profiles that carry no publication metadata.
     */
    public UMLProfileMetadata(String label, String uriPath, String version) {
        this(label, uriPath, version, "", "", "", "");
    }

    public String getLabel() {
        return this.label;
    }

    public String getUriPath() {
        return this.uriPath;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDate() {
        return this.date;
    }

    public String getComment() {
        return this.comment;
    }

    public String getCopyright() {
        return this.copyright;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'label: {1}, uriPath: {2}, version: {3}, author: {4}, date: {5}'}'";
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.label, this.uriPath, this.version, this.author, this.date);
    }
}