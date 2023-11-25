package com.example.springwebrest.web.funkos.controllers;

import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.services.CategoriaServicesImp;
import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.services.FunkoServices;
import com.example.springwebrest.utils.pagination.PaginationLinksUtils;
import com.example.springwebrest.web.funkos.store.UserStore;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/funkos")
public class FunkosWebController {

    private final FunkoServices funkoService;
    private final CategoriaServicesImp categoriaService;

    private final UserStore userSession;

    private final MessageSource messageSource;


    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkosWebController(FunkoServices funkoService, CategoriaServicesImp categoriaService, UserStore userSession, PaginationLinksUtils paginationLinksUtils,  MessageSource messageSource) {
        this.funkoService = funkoService;
        this.categoriaService = categoriaService;
        this.paginationLinksUtils = paginationLinksUtils;
        this.userSession = userSession;
        this.messageSource = messageSource;

    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (isLoggedAndSessionIsActive(session)) {
            return "redirect:/funkos";
        }
        return "funkos/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("password") String password, @RequestParam("username") String username,HttpSession session) {
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            userSession.setUsername(username);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return "redirect:/funkos";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/funkos";
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String indexListFunkos(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<Double> precio,
            @RequestParam(required = false) Optional<Integer> cantidad,
            @RequestParam(value = "search", required = false) Optional<String> search,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String order,
            Locale locale
    ) {

        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:/funkos/login";
        }

        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        sessionData.incrementLoginCount();
        var numVisitas = sessionData.getLoginCount();
        var lastLogin = sessionData.getLastLogin();
        var username = sessionData.getUsername();
        var localizedLastLoginDate = getLocalizedDate(lastLogin, locale);

        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var funkos = funkoService.findAll(search, precio, cantidad, categoria, pageable);
        String welcomeMessage = messageSource.getMessage("welcome.message", null, locale);
        model.addAttribute("welcomeMessage", welcomeMessage);
        model.addAttribute("funkos", funkos);
        model.addAttribute("numVisitas", numVisitas);
        model.addAttribute("lastLoginDate", localizedLastLoginDate);
        model.addAttribute("username", username);
        String categoriaStr = categoria.isPresent() ? categoria.get() : "";
        String nombreStr = search.isPresent() ? search.get() : "";
        String precioStr = precio.isPresent() ? precio.get().toString() : "";
        String cantidadStr = cantidad.isPresent() ? cantidad.get().toString() : "";


        model.addAttribute("paginationLinks", paginationLinksUtils.createLinkHeader(funkos, UriComponentsBuilder.fromUriString("/").queryParam("categoria", categoriaStr).queryParam("name", nombreStr).queryParam("price", precioStr).queryParam("quantity", cantidadStr)));
        model.addAttribute("categoria", categoriaStr);
        model.addAttribute("nombre", search.orElse(""));
        model.addAttribute("precio", precioStr);
        model.addAttribute("cantidad", cantidadStr);
        return "funkos/index";
    }

    @GetMapping("/details/{id}")
    public String detailsFunko(@PathVariable("id") Long id, Model model, HttpSession session){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:/funkos/login";
        }
        var funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funkos/details";
    }

    @GetMapping("/delete/{id}")
    public String deleteFunko(@PathVariable("id") Long id, HttpSession session){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:/login";
        }
        funkoService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/create")
    public String createFunko(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "ASC") String order
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:/funkos/login";
        }
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var categorias = categoriaService.findAll(Optional.empty(), Optional.of(true), pageable).map(Categoria::getTipo);
        model.addAttribute("funko", new FunkoUpdateRequest());
        model.addAttribute("categorias", categorias);
        return "funkos/create";
    }

    @PostMapping("/create")
    public String createPost(
            HttpSession session,
            @Valid @ModelAttribute("funko") FunkoCreateRequest funkoCreateRequest,
            BindingResult result,
            Model model
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:funkos/login";
        }
        if(result.hasErrors()){
            var categorias = categoriaService.findAll(Optional.empty(), Optional.of(true), Pageable.unpaged()).map(Categoria::getTipo);
            model.addAttribute("categorias", categorias);
            return "funkos/create";
        }

        funkoService.save(funkoCreateRequest);
        return "redirect:/funkos";
    }

    @GetMapping("/update/{id}")
    public String editFunko(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:funkos/login";
        }
        var funko = funkoService.findById(id);
        var categorias = categoriaService.findAll(Optional.empty(), Optional.of(true), Pageable.unpaged()).map(Categoria::getTipo);
        model.addAttribute("funko", funko);
        model.addAttribute("funkoupdaterequest", new FunkoUpdateRequest());
        model.addAttribute("categorias", categorias);
        return "funkos/update";
    }

    @PostMapping("/update/{id}")
    public String editPost(
            HttpSession session,
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("funkoCreateDto") FunkoUpdateRequest funkoUpdateRequest,
            BindingResult result,
            Model model
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:funkos/login";
        }
        if(result.hasErrors()){
            var categorias = categoriaService.findAll(Optional.empty(), Optional.of(true), Pageable.unpaged()).map(Categoria::getTipo);
            model.addAttribute("categorias", categorias);
            model.addAttribute("funko", funkoService.findById(id));
            return "funkos/update";
        }
        funkoService.update(id, funkoUpdateRequest);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String formEditImage(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:funkos/login";
        }
        var funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funkos/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String editImage(
            @PathVariable("id") Long id,
            @RequestParam("imagen") MultipartFile imagen,
            HttpSession session
    ){
        if (!isLoggedAndSessionIsActive(session)) {
            return "redirect:funkos/login";
        }
        funkoService.updateImage(id, imagen, true);
        return "redirect:/funkos";
    }

    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }

    private String getLocalizedDate(Date date, Locale locale) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(locale);
        return localDateTime.format(formatter);
    }
}