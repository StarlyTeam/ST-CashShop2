package net.starly.cashshop.database;

import net.starly.core.data.Config;

public class DatabaseContext {

    public static String TABLE_PREFIX;
    public static String CREATE_CASH_TABLE_QUERY;
    public static String CREATE_LOG_TABLE_QUERY;
    public static String PLAYER_CASH_INSERT;
    public static String PLAYER_CASH_CREATE;
    public static String PLAYER_CASH_LOG_INSERT;
    public static String PLAYER_CASH_GET;

    public static void initializingContext(Config config) {
        TABLE_PREFIX = config.getString("database.prefix") + "_";
        CREATE_CASH_TABLE_QUERY
                = "create table if not exists `"
                + TABLE_PREFIX +
                "cash` (`id` int auto_increment, `uniqueId` char(36) not null, `balance` long not null default '0', primary key(`id`)) default charset=utf8";
        CREATE_LOG_TABLE_QUERY
                = "create table if not exists `"
                + TABLE_PREFIX +
                "log` (`id` int primary key auto_increment, `user_id` int not null, `type` char(12), `source` text not null, `amount` long not null, `balance` long not null, `date` text not null, foreign key(`user_id`) references `"
                + TABLE_PREFIX +
                "cash` (`id`) on delete cascade) default charset=utf8";
        PLAYER_CASH_INSERT
                = "update `"
                + TABLE_PREFIX +
                "cash` set `balance`=? where `id`=?";
        PLAYER_CASH_CREATE
                = "insert into `"
                + TABLE_PREFIX +
                "cash` (`uniqueId`, `balance`) values(?, ?)";
        PLAYER_CASH_LOG_INSERT
                = "insert into `"
                + TABLE_PREFIX +
                "log` (`user_id`, `type`, `source`, `amount`, `balance`, `date`) values(?, ?, ?, ?, ?, ?)";
        PLAYER_CASH_GET
                = "select * from `"
                + TABLE_PREFIX +
                "cash` where `uniqueId`=?";

    }

    public static String generatingTableName(String table) {
        return TABLE_PREFIX + table;
    }

}
