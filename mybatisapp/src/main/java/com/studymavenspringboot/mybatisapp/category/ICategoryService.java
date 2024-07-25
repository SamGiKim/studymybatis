package com.studymavenspringboot.mybatisapp.category;
import com.studymavenspringboot.mybatisapp.ICommonService;

import java.util.List;

public interface ICategoryService<T> extends ICommonService<T> {
    ICategory findByName(String name);
    List<ICategory> findAllByNameContains(SearchCategoryDto dto);
    
}
