package com.gydx.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 拽小白
 */
@Component
public class RedisUtil {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MINUTES);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    public long getExpire(String key) {
        long res = 0;
        try {
            res = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis error: " + e);
        }
        return res;
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(keys));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 放入普通缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key
     * @param delta
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key
     * @param delta
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * HashGet
     *
     * @param key
     * @param item
     * @return
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 获取hashKey对应的所有键和值
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key
     * @param map
     * @param time
     * @return
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据，如果不存在将创建
     *
     * @param key
     * @param item
     * @param value
     * @return
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据并设置时间，如果不存在将创建
     *
     * @param key
     * @param item
     * @param value
     * @param time  要设置的时间，如果已存在的hash表有时间，则会覆盖原有的时间
     * @return
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key
     * @param items
     */
    public void hdel(String key, Object... items) {
        if (key != null && items != null)
            redisTemplate.opsForHash().delete(key, items);
        else
            throw new RuntimeException("key和item不能为空！");
    }

    /**
     * 判断hash表中是否存在该项
     *
     * @param key
     * @param item
     * @return
     */
    public boolean hHasKey(String key, String item) {
        if (key != null && item != null)
            return redisTemplate.opsForHash().hasKey(key, item);
        else
            throw new RuntimeException("key和item不能为空！");
    }

    /**
     * hash增加，如果不存在就会创建一个 并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by   要增加的值（正）
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash减少
     *
     * @param key
     * @param item
     * @param by   要减少的值（正）
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return hincr(key, item, -by);
    }

    /**
     * 根据key获取set中的所有值
     *
     * @param key
     * @return
     */
    public Set<?> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

    public Object sPop(String key) {
        try {
            return redisTemplate.opsForSet().pop(key);
        } catch (Exception e) {
            log.error("redis set pop error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据value从一个set中查询是否存在该值
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("redis error: ", e.getMessage());
            return false;
        }
    }

    /**
     * 得到key1和key2的并集并保存到key1中
     * @param key1
     * @param key2
     * @return
     */
    public long sUnion(String key1, String key2) {
        try {
            return redisTemplate.opsForSet().unionAndStore(key1, key2, key1);
        } catch (Exception e) {
            log.error("redis error: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 将一个或多个数据放入set
     *
     * @param key
     * @param values
     * @return
     */
    public long sset(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 将一个列表里的值都放入set
     * @param key
     * @param values
     */
    public void sset(String key, List<String> values) {
        for(String value : values) {
            sset(key, value);
        }
    }

    /**
     * 将一个或多个数据放入set，并设置时间
     *
     * @param key
     * @param time
     * @param values
     * @return
     */
    public long ssetWithExpire(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 获取该set的长度
     *
     * @param key
     * @return
     */
    public long sgetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 从set中删除一个或多个数值
     *
     * @param key
     * @param values
     * @return
     */
    public long sremove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 获取list [start, end] 范围的值，[0, -1] 代表所有值
     *
     * @param key
     * @param start 开始
     * @param end   结束
     * @return
     */
    public List<?> lget(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

    public List<?> lgetAll(String key) {
        try {
            return lget(key, 0, 1);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

    /**
     * 获取该list的长度
     *
     * @param key
     * @return
     */
    public long lgetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 通过索引获取list中的值 0开始的正着数 -1开始是倒着数 跟python一样
     *
     * @param key
     * @param index
     * @return
     */
    public Object lgetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

    /**
     * 往list右边放入值
     *
     * @param key
     * @param value
     * @return
     */
    public boolean lsetr(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 往list右边放入值并设置时间
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean lsetr(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 从list右边将values放入
     *
     * @param key
     * @param values
     * @return
     */
    public boolean lset(String key, List<?> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 从list右边将values放入并设置时间
     *
     * @param key
     * @param values
     * @param time
     * @return
     */
    public boolean lset(String key, List<?> values, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public boolean lupdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。
     * <p>
     * COUNT 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long lremove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 向有序集合（sorted set）添加一个成员 并设置时间
     * zadd key score1 member1 [score2 member2]
     *
     * @param key
     * @param member
     * @param score
     * @param time
     * @return
     */
    public boolean zadd(String key, Object member, double score, long time) {
        try {
            redisTemplate.opsForZSet().add(key, member, score);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return false;
        }
    }

    /**
     * 根据分数返回有序集合指定区间的成员
     *
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    public Set<?> zrangeByScore(String key, double minScore, double maxScore) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

    /**
     * 返回有序集合中某成员的分数
     *
     * @param key
     * @param member
     * @return
     */
    public double zscore(String key, Object member) {
        try {
            return redisTemplate.opsForZSet().score(key, member);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 返回有序集合中指定成员的索引值
     *
     * @param key
     * @param member
     * @return
     */
    public long zrank(String key, Object member) {
        try {
            return redisTemplate.opsForZSet().rank(key, member);
        } catch (Exception e) {
            log.error("redis error: " + e);
            return 0;
        }
    }

    /**
     * 迭代有序集合总的元素（包括元素成员和分数）
     *
     * @param key
     * @return
     */
    public Cursor<ZSetOperations.TypedTuple<Object>> zscan(String key) {
        try {
            Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan(key, ScanOptions.NONE);
            return cursor;
        } catch (Exception e) {
            log.error("redis error: " + e);
            return null;
        }
    }

}
