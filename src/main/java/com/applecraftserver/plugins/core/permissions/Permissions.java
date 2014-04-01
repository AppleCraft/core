package com.applecraftserver.plugins.core.permissions;

import com.applecraftserver.plugins.core.Core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.applecraftserver.plugins.core.AbstractPlugin.$;
import static com.applecraftserver.plugins.core.Core.logger;

public class Permissions {

    private final Core plugin;
    private Connection connection;
    private Permissions instance = this;

    public Permissions(Core core) {
        this.plugin = core;
        try {
            this.connection = core.getConnection();
            PreparedStatement countGroupsStatement = connection.prepareStatement("SELECT COUNT(id) FROM permission_groups");
            ResultSet countGroupsSet = countGroupsStatement.executeQuery();
            countGroupsSet.first();

            PreparedStatement countNodesStatement = connection.prepareStatement("SELECT COUNT(id) FROM permission_nodes");
            ResultSet countNodesSet = countNodesStatement.executeQuery();
            countNodesSet.first();

            PreparedStatement countPlayerGroupsStatement = connection.prepareStatement("SELECT p.*, g.* FROM `players` p join player_groups pg on p.id=pg.player join permission_groups g on pg.group=g.id;");
            ResultSet countPlayerGroupsSet = countPlayerGroupsStatement.executeQuery();
            countPlayerGroupsSet.first();
            int pgcount = 0;
            while (!countPlayerGroupsSet.isAfterLast()) {
                pgcount++;
                countPlayerGroupsSet.next();
            }

            $("perms", "Loaded " + countGroupsSet.getInt(1) + " permission groups and " + countNodesSet.getInt(1) + " nodes. " + pgcount + " players have been assigned groups.");
        } catch (SQLException ex) {
            logger.severe("Connection isn't working!");
            ex.printStackTrace();
        }
    }

    public Permissions getInstance() {
        return this.instance;
    }
}
