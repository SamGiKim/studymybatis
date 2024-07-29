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
            // java 에서 html 문자를 만드는 고전적인 방법은 매우 안 좋다.
            String sPages = this.getHtmlPageString(searchCategoryDto);
            model.addAttribute("pageHtml", sPages);
        } catch (Exception ex) {
            log.error(ex.toString());
            model.addAttribute("error_message", "오류가 발생했습니다. 관리자에게 문의하세요.");
            return "error/error_save";  // resources/templates 폴더안의 화면파일
        }
        return "catweb/category_list";  // resources/templates 폴더안의 화면파일
    }

    private String getHtmlPageString(SearchCategoryDto searchCategoryDto) {
        StringBuilder sResult = new StringBuilder();
        int tPage = (searchCategoryDto.getTotal() + 9) / 10;
        sResult.append("<div>");
        for ( int i = 0; i < tPage; i++ ) {
            sResult.append(" <a href='category_list?page=" + (i+1) +
                    "&name=" + searchCategoryDto.getName() + "'>");
            sResult.append(i+1);
            sResult.append("</a> ");
        }
        sResult.append("</div>");
        return sResult.toString();
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

    @GetMapping("/catweb_insert")
    public String catwebInsert(Model model, @RequestParam Long id) {
        try {
            if ( id == null || id <= 0 ) {
                model.addAttribute("error_message", "ID는 1보다 커야 합니다.");
                return "error/error_bad";
            }
            ICategory find = this.categoryService.findById(id);
            if ( find == null ) {
                model.addAttribute("error_message", id + " 데이터가 없습니다.");
                return "error/error_find";
            }
            model.addAttribute("categoryDto", find);
        } catch (Exception ex) {
            log.error(ex.toString());
            model.addAttribute("error_message", "서버 에러입니다. 관리자에게 문의 하세요.");
            return "error/error_save";  // resources/templates 폴더안의 화면파일
        }
        return "catweb/catweb_insert";
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
