package com.mall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IOrderService;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chuck Date:2017/7/30
 */
@Controller
@RequestMapping("/order/")
public class OrderController {


    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, long orderNo) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取购物车 客户在预览的时候看到的购物车明细
     * @param session
     * @return
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }


    /**
     * 订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 个人中心查看我的订单
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }










    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orederNo, HttpServletRequest request) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(), path, orederNo);
    }


    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + "";
            }
            params.put("name", valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求,验证不通过!");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }
        // 业务逻辑
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AliPayCallBack.RESPONSE_SUCCESS;
        }
        return Const.AliPayCallBack.RESPONSE_FAILED;
    }



    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orederNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orederNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

}

