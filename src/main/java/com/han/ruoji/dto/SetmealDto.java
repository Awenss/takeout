package com.han.ruoji.dto;


import com.han.ruoji.entity.Setmeal;
import com.han.ruoji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
