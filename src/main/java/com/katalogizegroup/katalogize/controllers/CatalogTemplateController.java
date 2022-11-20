package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.services.CatalogTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/CatalogTemplate")
public class CatalogTemplateController {

    @Autowired
    CatalogTemplateService catalogTemplateService;

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public CatalogTemplate createCatalogTemplate(@Argument CatalogTemplate catalogTemplate) {
        return catalogTemplateService.createCatalogTemplate(catalogTemplate);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CatalogTemplate> getAllCatalogTemplates() {
        return catalogTemplateService.getAllTemplates();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public CatalogTemplate getCatalogTemplateById(@Argument String id) {
        return catalogTemplateService.getTemplateById(id);
    }

    @SchemaMapping
    public List<CatalogTemplate> templates(Catalog catalog) {
        return catalogTemplateService.getAllTemplatesById(catalog.getTemplateIds());
    }

    @SchemaMapping
    public CatalogTemplate template(CatalogItem catalogItem) {
        return catalogTemplateService.getTemplateById(catalogItem.getTemplateId());
    }
}
