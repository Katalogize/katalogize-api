package com.katalogizegroup.katalogize.catalog;

import com.katalogizegroup.katalogize.user.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CatalogController {
    @QueryMapping
    public Catalog catalogById(@Argument String id) {
        return  Catalog.getById(id);
    }

    @SchemaMapping
    public User user(Catalog catalog) {
        return User.getById(catalog.getUserId());
    }
}
