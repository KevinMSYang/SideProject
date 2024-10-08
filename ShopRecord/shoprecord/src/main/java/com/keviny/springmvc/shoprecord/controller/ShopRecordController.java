package com.keviny.springmvc.shoprecord.controller;

import com.keviny.springmvc.shoprecord.entity.Shop;
import com.keviny.springmvc.shoprecord.entity.Store;
import com.keviny.springmvc.shoprecord.service.ShopService;
import com.keviny.springmvc.shoprecord.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/shoprecord")
public class ShopRecordController {
    private ShopService shopService;
    private StoreService storeService;

    @Autowired
    public ShopRecordController(ShopService theShopService, StoreService theStoreService) {
        shopService = theShopService;
        storeService = theStoreService;
    }

    @GetMapping("/list")
    public String listShops(
            @RequestParam(value = "sort", defaultValue = "item") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model theModel
    ) {
        List<Shop> shops;
        if ("desc".equalsIgnoreCase(direction)) {
            shops = shopService.findAllSortBy(sort, Sort.Direction.DESC);
        }
        else {
            shops = shopService.findAllSortBy(sort, Sort.Direction.ASC);
        }

        theModel.addAttribute("shops", shops);
        theModel.addAttribute("sortDirection", direction.equals("asc")? "desc" : "asc");
        return "shopdirectory/list-directory";
    }

//    @GetMapping("/list")
//    public String listDirectory(Model theModel) {
//        // get the shoplist from db
//        List<Shop> theShops = shopService.findAllShopList();
//        // add to the spring model
//        theModel.addAttribute("shops", theShops);
//        return "shopdirectory/list-directory";
//    }

    @GetMapping("/showFormForAddShop")
    public String showFormForAddShop(Model theModel) {
        // create model attribute to bind form data
        Shop theShop = new Shop();
        theModel.addAttribute("shops", theShop);
        return "shopdirectory/shop-AddUpdate";
    }

    @PostMapping("/saveShop")
    public String saveShopItem(@ModelAttribute("shops") Shop theShop) {
        shopService.saveShopItem(theShop);
        return "redirect:/shoprecord/list";
    }

    @GetMapping("/showFormForAddStore")
    public String showFormForAddStore(Model theModel) {
        Store theStore = new Store();
        theModel.addAttribute("stores", theStore);
        return "shopdirectory/store-AddUpdate";
    }

    @PostMapping("/saveStore")
    public String saveStore(@ModelAttribute("stores") Store theStore) {
        storeService.saveStore(theStore);
        return "redirect:/shoprecord/list";
    }

    @GetMapping("/showFormForShopUpdate")
    public String showFormForShopUpdate(@RequestParam("shopId") int theId, Model theModel) {
        // get the shop from the service
        Shop theShop = shopService.findById(theId);
        // set shop in the model to prepopulate the form
        theModel.addAttribute("shops", theShop);
        // send over to our form
        return "shopdirectory/shop-AddUpdate";
    }

    @GetMapping("/showFormForStoreUpdate")
    public String showFormForStoreUpdate(@RequestParam("storeId") int theId, Model theModel) {
        Store theStore = storeService.findById(theId);
        theModel.addAttribute("stores", theStore);
        return "shopdirectory/store-AddUpdate";
    }

    @GetMapping("/deleteShop")
    public String deleteShopItem(@RequestParam("shopId") int theId) {
        // delete the shop
        shopService.deleteById(theId);
        // redirect to the list
        return "redirect:/shoprecord/list";
    }

    @GetMapping("/deleteStore")
    public String deleteStore(@RequestParam("storeId") int theId) {
        storeService.deleteById(theId);
        return "redirect:/shoprecord/list";
    }

    @GetMapping("/searchFormForShop")
    public String searchFormForShop(Model theModel) {
        theModel.addAttribute("shopSearch", new Shop());
        return "shopdirectory/shop-Search";
    }

    @GetMapping("/searchShop")
    public String searchShop (@RequestParam("item") String item, Model theModel) {
        List<Shop> theShops = shopService.findByShopItem(item);
        theModel.addAttribute("shops", theShops);
        return "shopdirectory/shop-Search";
    }

    @GetMapping("/searchFormForStore")
    public String searchFormForStore(Model theModel) {
        theModel.addAttribute("storeSearch", new Store());
        return "shopdirectory/store-Search";
    }

    @GetMapping("/searchStore")
    public String searchStore(@RequestParam("store") String storeName, Model theModel) {
        Store theStore = storeService.findByStoreName(storeName);
        theModel.addAttribute("stores", theStore);
        return "shopdirectory/store-Search";
    }

    @GetMapping("/showFormForStore")
    public String showFormForStore(Model theModel) {
        List<Store> theStores = storeService.findAllStoreList();
        theModel.addAttribute("stores", theStores);
        return "shopdirectory/store-list.html";
    }

    @GetMapping("/spendChart")
    public String getSpendChart(Model theModel) {
        List<Shop> shopItems = shopService.findAllShopList(); // Fetch your shop items from service

        // separate date and sum up the price in certain date
        Map<String, Double> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        for (Shop eachShopItem : shopItems) {
            String formattedDate = sdf.format(eachShopItem.getDate());
            double price = Double.parseDouble(eachShopItem.getPrice());
            map.put(formattedDate, map.getOrDefault(formattedDate, 0.0) + price);
        }

        theModel.addAttribute("chartData", map);
        return "shopdirectory/google-charts";
    }
}
