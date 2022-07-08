package com.han.ruoji.dto;

import com.han.ruoji.entity.OrderDetail;
import com.han.ruoji.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
