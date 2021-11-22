package com.cooldrinkscompany.vmcs.pojo;


import java.util.List;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;

import javax.json.JsonBuilderFactory;

import io.helidon.dbclient.DbClient;



public class ProductDAOImpl implements ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());
    private DbClient dbClient;

    public ProductDAOImpl(DbClient dbClient) {
        this.dbClient = dbClient;

     // MySQL init
     dbClient.execute(handle -> handle
     .createInsert("CREATE TABLE public.drinks (id SERIAL PRIMARY KEY, name VARCHAR NULL)").execute())
     .thenAccept(System.out::println).exceptionally(throwable -> {
         LOGGER.log(Level.WARNING, "Failed to create table, maybe it already exists?", throwable);
         return null;
     });
    }    

    public List<Product> getAllProduct(){
        return null;
    }

    public Product getProduct(int ProductId){
        return null;

    }

    public void addProduct( Product product){

    }

    public void updateProduct( Product product){

    }

    public void deleteProduct( int ProductId){

    }

}    