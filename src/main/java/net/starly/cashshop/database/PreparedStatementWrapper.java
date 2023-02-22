package net.starly.cashshop.database;

import java.sql.PreparedStatement;

public class PreparedStatementWrapper {

    private PreparedStatement stmt;

    public PreparedStatementWrapper(PreparedStatement stmt) {
        this.stmt = stmt;
    }

}
