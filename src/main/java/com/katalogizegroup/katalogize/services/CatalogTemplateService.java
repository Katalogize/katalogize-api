package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.CatalogTemplate;
import com.katalogizegroup.katalogize.repositories.CatalogTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogTemplateService
{
    @Autowired
    CatalogTemplateRepository catalogTemplateRepository;

    public CatalogTemplate createCatalogTemplate(CatalogTemplate catalogTemplate) {
        catalogTemplate.setCreationDate(Instant.now());
        return catalogTemplateRepository.insert(catalogTemplate);
    }

    public CatalogTemplate saveCatalogTemplate(CatalogTemplate catalogTemplate) {
        return catalogTemplateRepository.save(catalogTemplate);
    }

    public CatalogTemplate deleteTemplateById(String id) {
        CatalogTemplate templateEntity = getTemplateById(id);
        if (templateEntity != null) {
            catalogTemplateRepository.deleteById(id);
            return templateEntity;
        }
        return null;
    }

    public List<CatalogTemplate> getAllTemplates() {
        return catalogTemplateRepository.findAll();
    }

    public List<CatalogTemplate> getAllTemplatesById (List<String> ids) {
        List<CatalogTemplate> templates = new ArrayList<>();
        if (ids != null) {
            for(CatalogTemplate template : catalogTemplateRepository.findAllById(ids)){
                templates.add(template);
            }
        }
        return templates;
    }
    public CatalogTemplate getTemplateById (String id) {
        return catalogTemplateRepository.findById(id).orElse(null);
    }
}
