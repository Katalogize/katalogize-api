package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogTemplateRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/CatalogTemplate")
public class CatalogTemplateController {

    @Autowired
    CatalogTemplateRepository catalogTemplateRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @MutationMapping
    public CatalogTemplate createCatalogTemplate(@Argument CatalogTemplate catalogTemplate) {
        catalogTemplate.setId((int)sequenceGenerator.generateSequence(CatalogTemplate.SEQUENCE_NAME));
        try {
            CatalogTemplate catalogTemplateEntity = catalogTemplateRepository.insert(catalogTemplate);
            return catalogTemplateEntity;
        } catch (Exception e){
            throw new GraphQLException("Error while creating the catalog template");
        }
    }

    @MutationMapping
    public CatalogTemplate deleteCatalogTemplate(@Argument int id) {
        Optional<CatalogTemplate> catalogTemplateEntity = catalogTemplateRepository.findById(id);
        if (!catalogTemplateEntity.isEmpty()) {
            catalogTemplateRepository.deleteById(id);
            return catalogTemplateEntity.get();
        }
        return null;
    }

    @QueryMapping
    public List<CatalogTemplate> getAllCatalogTemplates() {
        return catalogTemplateRepository.findAll();
    }

    @QueryMapping
    public Optional<CatalogTemplate> getCatalogTemplateById(@Argument int id) {
        return  catalogTemplateRepository.findById(id);
    }

    @SchemaMapping
    public List<CatalogTemplate> templates(Catalog catalog) {
        List <CatalogTemplate> templates = new ArrayList<>();
        for(CatalogTemplate template : catalogTemplateRepository.findAllById(catalog.getTemplateIds())){
            templates.add(template);
        }
        return templates;
    }
}
