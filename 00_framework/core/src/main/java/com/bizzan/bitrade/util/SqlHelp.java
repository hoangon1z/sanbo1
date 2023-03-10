package com.bizzan.bitrade.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bizzan.bitrade.annotation.NativeQuery;
import com.bizzan.bitrade.annotation.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author wmf
 * @date 2019-6-4 14:59:48
 */
@Slf4j
@Component
public class SqlHelp<D> {

    @PersistenceContext
    private EntityManager entityManager;

    public Object getOneField(NativeQuery q, Class clazz, String sql, String where) {
        Page<D> bySql = getBySql(q, clazz, sql, null, where);
        BigInteger re = (BigInteger) bySql.getContent();
        return re;
    }

    public D getOne(NativeQuery q, Class clazz, String sql, String where) {
        Page<D> bySql = getBySql(q, clazz, sql, null, where);
        List<D> re = (List<D>) bySql.getContent();
        if (re.size() != 1) {
            throw new RuntimeException("系统出错");
        }
        return re.get(0);
    }

    public List<D> getBySql(NativeQuery q, Class clazz, String sql, String where) {
        Page<D> page = getBySql(q, clazz, sql, null, where);
        return (List<D>) page.getContent();
    }

    public List<D> getBySql(Class clazz, String sql, String where) {
        Page<D> page = getBySql(null, clazz, sql, null, where);
        return (List<D>) page.getContent();
    }

    public Page<D> getBySql(NativeQuery q, Class clazz, String sql, Pageable pageable) {
        return getBySql(q, clazz, sql, pageable, "WHERE 1 = 1");
    }

    public Page<D> getSql(NativeQuery q, Class clazz, String sql, Pageable pageable) {
        return getBySql(q, clazz, sql, pageable, "");
    }

    /**
     * order 一定放在group后面
     *
     * @param q
     * @param clazz
     * @param sql
     * @param pageable
     * @param where
     * @return
     */
    public Page<D> getBySql(NativeQuery q, Class clazz, String sql, Pageable pageable, String where) {
        Boolean isGroup = sql.lastIndexOf("GROUP BY") != -1;
        ;
        Boolean isOrder = sql.lastIndexOf("ORDER BY") != -1;
        ;
        StringBuilder dataSql;
        StringBuilder groupSql;
        StringBuilder orderSql;
        if (isGroup) {
            String ds = sql.substring(0, sql.lastIndexOf("GROUP BY"));
            dataSql = new StringBuilder(ds);
        } else if (isOrder) {
            String ds = sql.substring(0, sql.lastIndexOf("ORDER BY"));
            dataSql = new StringBuilder(ds);
        } else {
            dataSql = new StringBuilder(sql);
        }
        StringBuilder whereSql = new StringBuilder(where);
        StringBuilder whereDynamic = getWhere(q);
        whereSql.append(whereDynamic);
        dataSql.append(" ").append(whereSql);
        if (isGroup) {
            String gs = sql.substring(sql.lastIndexOf("GROUP BY"));
            groupSql = new StringBuilder(gs);
            dataSql.append(" ").append(groupSql);
        } else if (isOrder) {
            String os = sql.substring(sql.lastIndexOf("ORDER BY"));
            orderSql = new StringBuilder(os);
            dataSql.append(" ").append(orderSql);
        }
        javax.persistence.Query dataQuery = entityManager.createNativeQuery(dataSql.toString());
        Long total = null;
        if (pageable != null) {
            StringBuilder countSql = new StringBuilder("select count(*) from (");
            countSql.append(dataSql).append(") s");
            javax.persistence.Query countQuery = entityManager.createNativeQuery(countSql.toString());
            dataQuery.setFirstResult((int) pageable.getOffset());
            dataQuery.setMaxResults(pageable.getPageSize());
            BigInteger count = (BigInteger) countQuery.getSingleResult();
            total = count.longValue();
        }
        List result = dataQuery.getResultList();
        List<D> rs = null;
        try {
            rs = VOHelp.listToVO(result, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        PageImpl<D> ds = new PageImpl<D>(rs, pageable, total);
        return ds;
    }

    private StringBuilder getWhere(NativeQuery query) {
        List<Predicate> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder("");
        if (query == null) {
            return builder;
        }
        try {
            List<Field> fields = VOHelp.getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String nativeName = q.nativeName();
                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    Object valString = val;
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }
                    if (fieldType == String.class || fieldType == Timestamp.class) {
                        valString = "'" + val.toString() + "'";
                    }
                    switch (q.type()) {
                        case EQUAL:
                            builder.append(" AND ").append(nativeName).append(" = ").append(valString);
                            break;
                        case GREATER_THAN:
                            builder.append(" AND ").append(nativeName).append(" > ").append(valString);
                            break;
                        case LESS_THAN:
                            builder.append(" AND ").append(nativeName).append(" < ").append(valString);
                            break;
                        case LESS_THAN_NQ:
                            builder.append(" AND ").append(nativeName).append(" <= ").append(valString);
                            break;
                        case INNER_LIKE:
                            ;
                            builder.append(" AND ").append(nativeName).append(" like ").append("'%").append(val).append("%'");
                            break;
                        case LEFT_LIKE:
                            builder.append(" AND ").append(nativeName).append(" like ").append("'%").append(val).append("'");
                            break;
                        case RIGHT_LIKE:
                            builder.append(" AND ").append(nativeName).append(" like ").append("'").append(val).append("%'");
                            break;
                        case IN:
                            Set<Long> vals = (Set<Long>) val;
                            if (CollUtil.isNotEmpty(vals)) {
                                Set<String> strings = new HashSet<>();
                                vals.forEach(v -> strings.add(String.valueOf(v)));
                                String join = String.join(",", strings);
                                builder.append(" AND ").append(nativeName)
                                        .append(" in (").append(join).append(")");
                            }
                            break;
                        default:
                            break;
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return builder;
    }
}
