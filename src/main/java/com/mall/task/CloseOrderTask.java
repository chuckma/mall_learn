package com.mall.task;

import com.mall.service.IOrderService;
import com.mall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Charles Date:2018/3/9
 */
@Component
@Slf4j
public class CloseOrderTask {


    @Autowired
    private IOrderService iOrderService;

    @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
       //  iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

}
