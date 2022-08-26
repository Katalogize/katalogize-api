package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.CatalogItem;
import com.katalogizegroup.katalogize.models.CatalogItemInput;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/Catalog")
public class CatalogItemController {
    @Autowired
    CatalogItemRepository catalogItemRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @MutationMapping
    public CatalogItem createCatalogItem(@Argument CatalogItemInput catalogItem) {
        CatalogItem createdCatalogItem = new CatalogItem((int)sequenceGenerator.generateSequence(CatalogItem.SEQUENCE_NAME), catalogItem.getCatalogId());
        if (catalogItem.getIntegerFields() != null) {
            for(ItemField field : catalogItem.getIntegerFields()) {
                createdCatalogItem.addField(field);
            }
        }
        if (catalogItem.getStringFields() != null) {
            for (ItemField field : catalogItem.getStringFields()) {
                createdCatalogItem.addField(field);
            }
        }
        try {
            CatalogItem catalogItemEntity = catalogItemRepository.insert(createdCatalogItem);
            return catalogItemEntity;
        } catch (Exception e){
            throw new GraphQLException("Error while creating the catalog item");
        }
    }

    @MutationMapping
    public CatalogItem deleteCatalogItem(@Argument int id) {
        Optional<CatalogItem> catalogItemEntity = catalogItemRepository.findById(id);
        if (!catalogItemEntity.isEmpty()) {
            catalogItemRepository.deleteById(id);
            return catalogItemEntity.get();
        }
        return null;
    }

    @QueryMapping
    public List<CatalogItem> getAllCatalogItems() {
        return catalogItemRepository.findAll();
    }

    @QueryMapping
    public Optional<CatalogItem> getCatalogItemById(@Argument int id) {
        return  catalogItemRepository.findById(id);
    }

    @QueryMapping
    public List<CatalogItem> getAllCatalogItemsByCatalogId(@Argument int id) {
        return catalogItemRepository.getCatalogItemsByCatalogId(id);
    }

    @SchemaMapping
    public List<ItemField> fields(CatalogItem catalogItem) {
        List<ItemField> itemFields = catalogItem.getFields();
        return itemFields;
    }
}
