package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.CatalogTemplateRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/CatalogItem")
public class CatalogItemController {
    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CatalogItemRepository catalogItemRepository;

    @Autowired
    CatalogTemplateRepository catalogTemplateRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public CatalogItem createCatalogItem(@Argument CatalogItemInput catalogItem) {
        Optional<CatalogItem> catalogItemExists = catalogItemRepository.findById(catalogItem.getId());
        if (catalogItemExists.isEmpty()) catalogItem.setId(new ObjectId().toString());
        if (catalogItem.getName().equals("create-item") || catalogItem.getName().equals("")) throw new GraphQLException("Invalid Item name");
        CatalogItem catalogItemNameExists = catalogItemRepository.getCatalogItemByNameAndCatalogId(catalogItem.getName(), catalogItem.getCatalogId());
        if (catalogItemNameExists != null && !catalogItemNameExists.getId().equals(catalogItem.getId())) throw new GraphQLException("An item with this name already exists in this catalog");
        Optional<Catalog> catalog = catalogRepository.findById(catalogItem.getCatalogId());
        Optional<CatalogTemplate> template = catalogTemplateRepository.findById((catalogItem.getTemplateId()));
        //Map fields by template
        if (!catalog.isEmpty() && !template.isEmpty() && catalog.get().getTemplateIds().contains(catalogItem.getTemplateId())) {
            CatalogItem createdCatalogItem = new CatalogItem(catalogItem.getCatalogId(), catalogItem.getTemplateId(), catalogItem.getName(), new ArrayList<>());
            if (!catalogItemExists.isEmpty()) createdCatalogItem.setId(catalogItemExists.get().getId());
            //Ensure all template fields are present
            for(TemplateField templateField : template.get().getTemplateFields()) {
                switch (templateField.getFieldType()){
                    case 2:
                        Optional<ItemFieldInt> itemInt;
                        itemInt = catalogItem.getIntegerFields().stream().filter(field -> field.getOrder() == templateField.getOrder()).findFirst();
                        if (!itemInt.isEmpty()) {
                            createdCatalogItem.addField(itemInt.get());
                            catalogItem.getIntegerFields().remove(itemInt.get());
                        } else {
                            throw new GraphQLException("Catalog not following the right template order");
                        }
                        break;
                    case 1:
                        Optional<ItemFieldString> itemString;
                        itemString = catalogItem.getStringFields().stream().filter(field -> field.getOrder() == templateField.getOrder()).findFirst();
                        if (!itemString.isEmpty()) {
                            createdCatalogItem.addField(itemString.get());
                            catalogItem.getStringFields().remove(itemString.get());
                        } else {
                            throw new GraphQLException("Catalog not following the right template order");
                        }
                        break;
                    default:
                        throw new GraphQLException("Invalid template field");
                }
            }
            //Add non template fields
            if (!catalogItem.getIntegerFields().isEmpty() || !catalogItem.getStringFields().isEmpty()) {
                if (template.get().isAllowNewFields()) {
                    while (!catalogItem.getIntegerFields().isEmpty() || !catalogItem.getStringFields().isEmpty()) {
                        if (!catalogItem.getIntegerFields().isEmpty()) {
                            Optional<ItemField> existentItemOrder = createdCatalogItem.getFields().stream().filter(field -> field.getOrder() == catalogItem.getIntegerFields().get(0).getOrder()).findFirst();
                            if (existentItemOrder.isEmpty()) {
                                createdCatalogItem.addField(catalogItem.getIntegerFields().get(0));
                                catalogItem.getIntegerFields().remove(catalogItem.getIntegerFields().get(0));
                            } else {
                                throw new GraphQLException("Repeated order found on item fields");
                            }
                        }
                        if (!catalogItem.getStringFields().isEmpty()) {
                            Optional<ItemField> existentItemOrder = createdCatalogItem.getFields().stream().filter(field -> field.getOrder() == catalogItem.getStringFields().get(0).getOrder()).findFirst();
                            if (existentItemOrder.isEmpty()) {
                                createdCatalogItem.addField(catalogItem.getStringFields().get(0));
                                catalogItem.getStringFields().remove(catalogItem.getStringFields().get(0));
                            } else {
                                throw new GraphQLException("Repeated order found on item fields");
                            }
                        }
                    }
                } else {
                    throw new GraphQLException("Additional fields not allowed");
                }
            }

            //Create and save catalog
            CatalogItem catalogItemEntity = catalogItemRepository.save(createdCatalogItem);
            return catalogItemEntity;
        } else {
            throw new GraphQLException("Catalog or Template does not exist or are not associated");
        }
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public CatalogItem deleteCatalogItem(@Argument String id) {
        Optional<CatalogItem> catalogItemEntity = catalogItemRepository.findById(id);
        boolean isAdmin = false;
        String userId = "";
        if (!catalogItemEntity.isEmpty()) {
            //Check if user is allowed to delete
            Optional<Catalog> catalogEntity = catalogRepository.findById(catalogItemEntity.get().getCatalogId());
            try {
                UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
                userId = userDetails.getId();
            } finally {
                if ((catalogEntity.get().getUserId().equals(userId)) || isAdmin) {
                    catalogItemRepository.deleteById(id);
                    return catalogItemEntity.get();
                } else {
                    throw new GraphQLException("Unauthorized");
                }
            }
        }
        return null;
    }

    @QueryMapping
    public List<CatalogItem> getAllCatalogItems() {
        return catalogItemRepository.findAll();
    }

    @QueryMapping
    public CatalogItem getCatalogItem(@Argument String username, @Argument String catalogName, @Argument String itemName) {
        Optional<User> user = userRepository.getUserByUsername(username);
        if (user.isEmpty()) throw new GraphQLException("Invalid user");
        Catalog catalog = catalogRepository.getCatalogByUserIdAndCatalogName(user.get().getId(), catalogName);
        if (catalog == null) throw new GraphQLException("Invalid catalog");
        CatalogItem catalogItem = catalogItemRepository.getCatalogItemByNameAndCatalogId(itemName, catalog.getId());
        if (catalogItem == null) throw new GraphQLException("Invalid item");
        return catalogItem;
    }

    @QueryMapping
    public Optional<CatalogItem> getCatalogItemById(@Argument String id) {
        return  catalogItemRepository.findById(id);
    }

    @QueryMapping
    public List<CatalogItem> getAllCatalogItemsByCatalogId(@Argument String id) {
        return catalogItemRepository.getCatalogItemsByCatalogId(id);
    }

    @SchemaMapping
    public List<CatalogItem> items(Catalog catalog) {
        List<CatalogItem> catalogItems = getAllCatalogItemsByCatalogId(catalog.getId());
        return catalogItems;
    }

    @SchemaMapping
    public List<ItemField> fields(CatalogItem catalogItem) {
        List<ItemField> itemFields = catalogItem.getFields();
        Optional<CatalogTemplate> template = catalogTemplateRepository.findById(catalogItem.getTemplateId());
        if (!template.isEmpty()) {
            CatalogTemplate templateEntity = template.get();
            for (ItemField item : itemFields) {
                if (item.getOrder() != -1) {
                    Optional<TemplateField> templateField = templateEntity.getTemplateFields().stream().filter(field -> field.getOrder() == item.getOrder()).findFirst();
                    if (!templateField.isEmpty()) {
                        item.setName(templateField.get().getName());
                    }
                }
            }
        }
        return itemFields;
    }
}
