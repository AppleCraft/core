package com.applecraftserver.plugins.core.util;

import com.applecraftserver.plugins.core.Core;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {

	public static Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}

	public static boolean incrementDatabaseInt(String table, String column, int rowid) {
		try {
			Connection connection = Core.instance.getConnection();
			PreparedStatement get = connection.prepareStatement("SELECT ? FROM ? WHERE id=? LIMIT 1");
			get.setString(0, column);
			get.setString(1, table);
			get.setInt(2, rowid);
			ResultSet getresult = get.executeQuery();
			getresult.first();
			int old = getresult.getInt(1);

			PreparedStatement update = connection.prepareStatement("UPDATE ? SET ?=? WHERE ?.id=? LIMIT 1");
			update.setString(0, table);
			update.setString(1, column);
			update.setInt(2, old + 1);
			update.setString(3, table);
			update.setInt(4, rowid);
			ResultSet result = update.executeQuery();
		} catch (SQLException ex) {
			return false;
		}
		return true;
	}
}
