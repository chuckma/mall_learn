package com.mall.task;

import com.mall.common.Const;
import com.mall.service.IOrderService;
import com.mall.util.PropertiesUtil;
import com.mall.util.RedisShardedPoolUtil;
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

   // @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
       //  iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }


    @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeOut));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            // 如果返回值在是1 ,代表设置成功,获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单定时任务结束");
    }


    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 50);// 设置有效期 50 秒 防止死锁, 线上生产环境应该设置为 5 秒
        log.info("获取{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
        // iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());

        log.info("********************************************");
    }

}
