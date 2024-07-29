package com.studymavenspringboot.mybatisapp.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/catweb")
public class CatWebController {
    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("")
    public String indexHome(){
        return "index";
    }
    @GetMapping("/category_list")
    public String catwebList(Model model, @RequestParam String name, @RequestParam int page) {
        try {
            if (name == null) {
                name = "";
            }
            SearchCategoryDto searchCategoryDto = SearchCategoryDto.builder()
                    .name(name).page(page).build();
            int total = this.categoryService.countAllByNameContains(searchCategoryDto);
            searchCategoryDto.setTotal(total);
            List<ICategory> allList = this.categoryService.findAllByNameContains(searchCategoryDto);
            model.addAttribute("itemList", allList);
            model.addAttribute("searchCategoryDto", searchCategoryDto);
        } catch (Exception ex) {
            log.error(ex.toString());
            model.addAttribute("error_message", "오류가 발생했습니다. 관리자에게 문의하세요.");
            return "error/error_save";
        }
        return "catweb/category_list";
    }

    @GetMapping("/category_add")
    public String categoryAdd(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        // 카테고리 추가화면 템플릿엔진에서 출력
        return "catweb/category_add";
    }
    @PostMapping("/category_insert")
    public String categoryInsert(@ModelAttribute CategoryDto dto, Model model) {
        try {
            if ( dto == null || dto.getName() == null || dto.getName().isEmpty() ) {
                model.addAttribute("error_message", "이름이 비었습니다.");
                return "error/error_bad";  // resources/templates 폴더안의 화면파일
            }
            this.categoryService.insert(dto);
        } catch (Exception ex) {
            log.error(ex.toString());
            model.addAttribute("error_message", dto.getName() + " 중복입니다.");
            return "error/error_save";
        }
        return "redirect:category_list?page=1&name=";
    }

    @PostMapping("/catweb_update")
    public String catwebUpdate(@ModelAttribute CategoryDto dto, Model model){
        try{
            if(dto==null || dto.getId() <= 0 || dto.getName().isEmpty()){
                model.addAttribute("error_message", "id는 1보다 커야하고, name이 있어야합니다.");
                return "error/error_bad";
            }
            ICategory find = this.categoryService.findById(dto.getId());
            if (find == null) {
                model.addAttribute("error_message", dto.getId() + " 데이터가 없습니다.");
                return "error/error_find";
            }
            this.categoryService.update(dto.getId(), dto);
        } catch (Exception ex) {
            log.error(ex.toString());
            model.addAttribute("error_message", dto.getName() + " 중복입니다.");
            return "error/error_save";
        }
        return "redirect:category_list?page=1&name=";
    }


    @GetMapping("/catweb_delete")
    public String categoryDelete(@RequestParam Long id, Model model){
        try{
            if(id==null || id <= 0){
                model.addAttribute("error_message", "id는 1보다 커야합니다.");
                return "error/error_bad";
            }
            ICategory find = this.categoryService.findById(id);
            if (find == null) {
                model.addAttribute("error_message", id + " 데이터가 없습니다.");
                return "error/error_find";
            }
            this.categoryService.delete(id);
        } catch (Exception ex){
            log.error(ex.toString());
            model.addAttribute("error_message", "서버 에러입니다. 관리자에게 문의 하세요.");
            return "error/error_save";
        }
        return "redirect:category_list?page=1&name=";
    }
}
