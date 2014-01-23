package com.applecraftserver.plugins.core.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class StringUtils {

	public static String getLocString(Location l) {
		if (l == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		sb.append((MathUtils.isInt(l.getX())) ? l.getBlockX() : l.getX()).append(",");
		sb.append((MathUtils.isInt(l.getY())) ? l.getBlockY() : l.getY()).append(",");
		sb.append(((MathUtils.isInt(l.getZ())) ? l.getBlockZ() : l.getZ()));
		if (l.getYaw() != 0f) {
			sb.append(",").append(l.getYaw());
		}
		if (l.getPitch() != 0f) {
			sb.append(",").append(l.getPitch());
		}
		return sb.toString();
	}

	public static String colorize(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String formatDate(long timestamp) {
		return new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm aaa z").format(timestamp);
	}

	public static String getJoinedArgsAfter(List<String> args, int index) {
		return org.apache.commons.lang.StringUtils.join(args.subList(index, args.size()), " ");
	}

	public static int dlDistance(String a, String b) {
		final int INFINITY = a.length() + b.length();
		int[][] H = new int[a.length() + 2][b.length() + 2];
		H[0][0] = INFINITY;
		for (int i = 0; i <= a.length(); i++) {
			H[i + 1][1] = i;
			H[i + 1][0] = INFINITY;
		}
		for (int j = 0; j <= b.length(); j++) {
			H[1][j + 1] = j;
			H[0][j + 1] = INFINITY;
		}
		int[] DA = new int[999];
		Arrays.fill(DA, 0);
		for (int i = 1; i <= a.length(); i++) {
			int DB = 0;
			for (int j = 1; j <= b.length(); j++) {
				int i1 = DA[b.charAt(j - 1)];
				int j1 = DB;
				int d = ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1);
				if (d == 0) {
					DB = j;
				}
				H[i + 1][j + 1] =
						MathUtils.min(H[i][j] + d,
						H[i + 1][j] + 1,
						H[i][j + 1] + 1,
						H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
			}
			DA[a.charAt(i - 1)] = i;
		}
		return H[a.length() + 1][b.length() + 1];

	}
}
