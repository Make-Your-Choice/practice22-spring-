package com.yav.pract.controllers;

import com.yav.pract.dao.currencyDAO;
import com.yav.pract.models.currency;
import com.yav.pract.parsers.currencyParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/currency")
public class currencyController {
    private currencyDAO currencyDao;
    private currencyParser parser;
    @Autowired
    public currencyController(currencyDAO currencyDao, currencyParser parser) {
        this.currencyDao = currencyDao;
        this.parser = parser;
    }
    /*@GetMapping("/hhh")
    public String index(Model model) {
        model.addAttribute("currency", currencyDao.index());
        return "currency/hhh";
    }*/
    @GetMapping("/all")
    public String showByDate(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy")
                             Date date, Model model) throws IOException, SAXException, ParserConfigurationException, ParseException {
        if(currencyDao.showByDate(date).isEmpty()) {
            parser.parseByDate(currencyDao, date);
        }
        model.addAttribute("currency", currencyDao.showByDate(date));
        return "currency/all";
    }
    @GetMapping("/one")
    public String showByPeriod(@RequestParam("date1") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date1,
                               @RequestParam("date2") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date2,
                               @RequestParam("cbid") String cbid, Model model) throws ParseException, IOException, ParserConfigurationException, SAXException {
        List<currency> currList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        currency curr;
        while(date1.compareTo(date2)<=0) {
            curr = currencyDao.showByDateCbId(date1, cbid);
            if(curr == null) {
                parser.parseByDate(currencyDao, date1);
                curr = currencyDao.showByDateCbId(date1, cbid);
            }
            currList.add(curr);
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date1 = calendar.getTime();
        }
        model.addAttribute("currency", currList);
        return "currency/one";
    }
    /*public String index() {
        return "currency/hhh";
    }*/
}
