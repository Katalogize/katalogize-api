package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.inputs.CatalogItemInput;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldNumber;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import graphql.GraphQLException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogItemService {
    @Autowired
    CatalogItemRepository catalogItemRepository;

    @Autowired
    CatalogTemplateService catalogTemplateService;

    @Autowired
    CatalogService catalogService;

    @Autowired
    UserService userService;

    @Autowired
    UploadFileService uploadFileService;

    public CatalogItem saveCatalogItem(CatalogItemInput catalogItem) {
        CatalogItem catalogItemExists = getCatalogItemById(catalogItem.getId());
        if (catalogItemExists == null) catalogItem.setId(new ObjectId().toString());
        if (catalogItem.getName().equals("create-item") || catalogItem.getName().equals("")) throw new GraphQLException("Invalid Item name");
        CatalogItem catalogItemNameExists = catalogItemRepository.getCatalogItemByNameAndCatalogId(catalogItem.getName(), catalogItem.getCatalogId());
        if (catalogItemNameExists != null && !catalogItemNameExists.getId().equals(catalogItem.getId())) throw new GraphQLException("An item with this name already exists in this catalog");
        Catalog catalog = catalogService.getCatalogById(catalogItem.getCatalogId());
        CatalogTemplate template = catalogTemplateService.getTemplateById(catalogItem.getTemplateId());
        User loggedUser = userService.getLoggedUser();
        //Map fields by template
        if (catalog != null && template != null && catalog.getTemplateIds().contains(catalogItem.getTemplateId())) {
            int userPermission = catalog.setUserPermission(loggedUser);
            if (userPermission < 2) throw new GraphQLException("Unauthorized");
            CatalogItem createdCatalogItem = new CatalogItem(catalogItem.getCatalogId(), catalogItem.getTemplateId(), catalogItem.getName(), new ArrayList<>());
            if (catalogItemExists != null) {
                createdCatalogItem.setId(catalogItemExists.getId());
            }
            //Ensure all template fields are present
            for(TemplateField templateField : template.getTemplateFields()) {
                switch (templateField.getFieldType()) {
                    case 1 -> {
                        Optional<ItemFieldString> itemString;
                        itemString = catalogItem.getStringFields().stream().filter(field -> field.getTemplateFieldId().equals(templateField.getId())).findFirst();
                        if (itemString.isPresent()) { //Exists in template
                            createdCatalogItem.addField(itemString.get());
                            catalogItem.getStringFields().remove(itemString.get());
                        } else {
                            throw new GraphQLException("Catalog not following the right template order");
                        }
                    }
                    case 2 -> {
                        Optional<ItemFieldNumber> itemInt;
                        itemInt = catalogItem.getNumberFields().stream().filter(field -> field.getTemplateFieldId().equals(templateField.getId())).findFirst();
                        if (itemInt.isPresent()) {
                            createdCatalogItem.addField(itemInt.get());
                            catalogItem.getNumberFields().remove(itemInt.get());
                        } else {
                            throw new GraphQLException("Catalog not following the right template order");
                        }
                    }
                    case 3 -> {
                        Optional<ItemFieldImage> itemImage;
                        itemImage = catalogItem.getImageFields().stream().filter(field -> field.getTemplateFieldId().equals(templateField.getId())).findFirst();
                        List<UploadFile> existentImages = new ArrayList<>(); //Get existent images for this field
                        if (catalogItemExists != null) {
                            Optional<ItemField> existentFields = catalogItemExists.getFields().stream().filter(field -> field.getTemplateFieldId().equals(templateField.getId())).findFirst();
                            if (existentFields.isPresent())
                                existentImages = ((ItemFieldImage) existentFields.get()).getValue();
                        }
                        if (itemImage.isPresent()) { //Add images and upload the ones that have data
                            ItemFieldImage images = itemImage.get();
                            List<UploadFile> files = new ArrayList<>();
                            if (images.getValue() == null) images.setValue(new ArrayList<>());
                            for (UploadFile file : images.getValue()) {
                                if (file.getData() != null) {
                                    String filePath = uploadFileService.uploadFile(catalog.getUserId() + "/" + catalogItem.getCatalogId() + "/" + createdCatalogItem.getId(), "item", file.getData()); //Upload new image
                                    files.add(new UploadFile(filePath, null));
                                } else {
                                    files.add(new UploadFile(file.getPath(), null));
                                }
                            }
                            if (!existentImages.isEmpty()) { //Delete missing images
                                for (UploadFile image : existentImages) {
                                    Optional<UploadFile> addedImage = files.stream().filter(file -> file.getPath().equals(image.getPath())).findFirst();
                                    if (addedImage.isEmpty()) {
                                        uploadFileService.deleteFile(image.getPath());
                                    }
                                }
                            }
                            images.setValue(files);
                            createdCatalogItem.addField(images);
                            catalogItem.getImageFields().remove(itemImage.get());
                        } else {
                            throw new GraphQLException("Catalog not following the right template order");
                        }
                    }
                    default -> throw new GraphQLException("Invalid template field");
                }
            }
            //Add non template fields
            if (!catalogItem.getNumberFields().isEmpty() || !catalogItem.getStringFields().isEmpty()) {
                if (template.isAllowNewFields()) {
                    while (!catalogItem.getNumberFields().isEmpty() || !catalogItem.getStringFields().isEmpty()) {
                        if (!catalogItem.getNumberFields().isEmpty()) {
                            Optional<ItemField> existentItemOrder = createdCatalogItem.getFields().stream().filter(field -> field.getTemplateFieldId().equals(catalogItem.getNumberFields().get(0).getTemplateFieldId())).findFirst();
                            if (existentItemOrder.isEmpty()) {
                                createdCatalogItem.addField(catalogItem.getNumberFields().get(0));
                                catalogItem.getNumberFields().remove(catalogItem.getNumberFields().get(0));
                            } else {
                                throw new GraphQLException("Repeated order found on item fields");
                            }
                        }
                        if (!catalogItem.getStringFields().isEmpty()) {
                            Optional<ItemField> existentItemOrder = createdCatalogItem.getFields().stream().filter(field -> field.getTemplateFieldId().equals(catalogItem.getStringFields().get(0).getTemplateFieldId())).findFirst();
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
            catalogItemEntity.setUserPermission(catalog.getUserPermission());
            return catalogItemEntity;
        } else {
            throw new GraphQLException("Catalog or Template does not exist or are not associated");
        }
    }

    public CatalogItem deleteCatalogItem(String id) {
        CatalogItem catalogItem = getCatalogItemById(id);
        User loggedUser;
        if (catalogItem != null) {
            Catalog catalog = catalogService.getCatalogById(catalogItem.getCatalogId());
            try {
                loggedUser = userService.getLoggedUser();
            } catch (Exception e) {
                loggedUser = null;
            }
            catalog.setUserPermission(loggedUser);
            if (catalog.getUserPermission() >= 2) {
                List<ItemField> imagesField = catalogItem.getFields().stream().filter(field -> field.getClass() == ItemFieldImage.class).toList();
                for (ItemField field: imagesField) {
                    for (UploadFile image : ((ItemFieldImage) field).getValue()) {
                        uploadFileService.deleteFile(image.getPath());
                    }
                }
                catalogItemRepository.deleteById(id);
                return catalogItem;
            } else {
                throw new GraphQLException("Unauthorized");
            }
        }
        return null;
    }

    public CatalogItem getCatalogItem(String username, String catalogName, String itemName) {
        User owner = userService.getByUsername(username);
        if (owner == null) throw new GraphQLException("Invalid user");
        Catalog catalog = catalogService.getCatalogByUsernameAndCatalogName(username, catalogName);
        CatalogItem catalogItem = catalogItemRepository.getCatalogItemByNameAndCatalogId(itemName, catalog.getId());
        if (catalogItem == null) throw new GraphQLException("Invalid item");
        catalogItem.setUserPermission(catalog.getUserPermission());
        return catalogItem;
    }

    public List<CatalogItem> getAllCatalogItemsByCatalogId(String id) {
        return catalogItemRepository.getCatalogItemsByCatalogId(id);
    }

    public List<ItemField> getFieldsFromItem(CatalogItem catalogItem) {
        List<ItemField> itemFields = catalogItem.getFields();
        CatalogTemplate template = catalogTemplateService.getTemplateById(catalogItem.getTemplateId());
        if (template != null) {
            for (ItemField item : itemFields) {
                Optional<TemplateField> templateField = template.getTemplateFields().stream().filter(field -> field.getId().equals(item.getTemplateFieldId())).findFirst();
                templateField.ifPresent(field -> item.setName(field.getName()));
            }
        }
        return itemFields;
    }

    public List<UploadFile> getValuesFromImageField(ItemFieldImage itemImage) {
        return new ArrayList<>(itemImage.getValue());
    }

    public List<CatalogItem> getAllCatalogItems() {
        return catalogItemRepository.findAll();
    }

    public CatalogItem getCatalogItemById (String id) {
        return catalogItemRepository.findById(id).orElse(null);
    }
}
