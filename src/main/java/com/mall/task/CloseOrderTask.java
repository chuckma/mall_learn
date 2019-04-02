package com.mall.task;

import com.mall.common.Const;
import com.mall.common.RedissonManager;
import com.mall.service.IOrderService;
import com.mall.util.PropertiesUtil;
import com.mall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Created by Charles Date:2018/3/9
 */
@Component
@Slf4j
public class CloseOrderTask {


    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManager redissonManager;

    @PreDestroy
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }

   // @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
       //  iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }


    // @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
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


    @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务启动");
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeOut));
        if (setnxResult != null && setnxResult.intValue() == 1) {
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            // 未获取到锁,继续判断时间戳,看是否可以重置并获取到锁
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            // 若果当前的获取到的 lockValueStr 不为空，并且当前的时间 > lockValueStr ，说明这个锁已经超时了，是可以获取锁的。
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                // 重新上锁
                String getSetResult = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeOut));
                //再次用当前时间戳getSet
                //返回给定的 key 旧值, > 旧值判断,是否可以获取锁
                //当 key 没有旧值得时候, 即key 不存在的时候,返回 nil -> 获取锁
                //这里我们set了一个新的value 值,获取旧的值
                if (getSetResult == null || (getSetResult != null && StringUtils.equals(lockValueStr, getSetResult))) {
                    /**
                     * getSetResult != null && StringUtils.equals(lockValueStr, getSetResult)
                     * 重新设置锁的过程中，锁没有变化,可以获取锁
                     */
                    //真正获取到锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务结束");
    }

   //  @Scheduled(cron = "0 */1 * * * ?")// 每 1 分钟执行一次 (1 分钟的整数倍)
    public void closeOrderTaskV4() {

        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            if (getLock = lock.tryLock(0, 50, TimeUnit.SECONDS)) {
                log.info("Redisson 获取到分布式锁:{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
//                iOrderService.closeOrder(hour);
            } else {
                log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        } catch (InterruptedException e) {
            log.error("Redisson 获取分布式锁异常",e);
        }finally {
            if (!getLock) {
                return;
            }
            lock.unlock();
            log.info("Redisson 分布式释放锁");
        }

    }


    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 5);// 设置有效期 50 秒 防止死锁, 线上生产环境应该设置为 5 秒
        log.info("获取{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.out", "2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());

        log.info("********************************************");
    }

}
