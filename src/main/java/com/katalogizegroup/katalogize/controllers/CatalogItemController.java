package com.katalogizegroup.katalogize.controllers;


import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.inputs.CatalogItemInput;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.services.CatalogItemService;
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
@RequestMapping(value = "/CatalogItem")
public class CatalogItemController {

    @Autowired
    CatalogItemService catalogItemService;

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public CatalogItem saveCatalogItem(@Argument CatalogItemInput catalogItem) {
        return catalogItemService.saveCatalogItem(catalogItem);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public CatalogItem deleteCatalogItem(@Argument String id) {
        return catalogItemService.deleteCatalogItem(id);
    }

    @QueryMapping
    public CatalogItem getCatalogItem(@Argument String username, @Argument String catalogName, @Argument String itemName) {
        return catalogItemService.getCatalogItem(username, catalogName, itemName);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CatalogItem> getAllCatalogItems() {
        return catalogItemService.getAllCatalogItems();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public CatalogItem getCatalogItemById(@Argument String id) {
        return catalogItemService.getCatalogItemById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CatalogItem> getAllCatalogItemsByCatalogId(@Argument String id) {
        return catalogItemService.getAllCatalogItemsByCatalogId(id);
    }

    @SchemaMapping
    public List<CatalogItem> items(Catalog catalog) {
        return catalogItemService.getAllCatalogItemsByCatalogId(catalog.getId());
    }

    @SchemaMapping
    public List<ItemField> fields(CatalogItem catalogItem) {
        return catalogItemService.getFieldsFromItem(catalogItem);
    }

    @SchemaMapping
    public List<UploadFile> value(ItemFieldImage itemImage) {
        return catalogItemService.getValuesFromImageField(itemImage);
    }
}
