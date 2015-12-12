package starfighter;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.io.ByteStreams;

public class Disasm {

	public static void main(String[] args) throws IOException, InterruptedException {
		Path dir = Paths.get("src/main/java/avr");
		List<String> lines = Files.readAllLines(dir.resolve("in.txt"));

		List<AsmStmt> stmts = new LinkedList<>();
		for (String line : lines) {
			System.out.println("line = " + line);
			parseAsmStmt(stmts, line);
		}

		for (AsmStmt stmt : stmts) {
			stmt.arg1 = unify(stmt.arg1);
			stmt.arg2 = unify(stmt.arg2);
		}

		Map<Integer, Integer> adr2Idx = new HashMap<>();
		for (int i = 0; i < stmts.size(); i++) {
			AsmStmt stmt = stmts.get(i);
			adr2Idx.put(stmt.adr, i);
			System.out.println("stmt = " + stmt);
		}

		int N = stmts.size();
		int[][] g = new int[N][N];
		for (int i = 0; i < stmts.size(); i++) {
			AsmStmt stmt = stmts.get(i);
			if (i > 0) {
				g[i - 1][i] = 1;
			}

			if (stmt.arg1 != null && stmt.arg1.startsWith(".")) {
				String relAdr = stmt.arg1.substring(1);
				String direction = relAdr.substring(0, 1);
				relAdr = relAdr.substring(1);
				int value = Integer.parseInt(relAdr, 10);
				int targetAdr = stmts.get(i + 1).adr + (value * (direction.equals("+") ? 1 : -1));
				Integer targetIdx = adr2Idx.get(targetAdr);
				if (targetIdx == null) {
					throw new AssertionError(i);
				}
				g[i][targetIdx] = 1;
			}
		}

		Map<String, String> reg2Color = new HashMap<>();
		int[] curCol = {1, 1, 1};
		for (AsmStmt stmt : stmts) {
			if (stmt.arg1 != null && stmt.arg1.startsWith("r") && reg2Color.get(stmt.arg1) == null) {
				curCol = defColor(curCol);
				reg2Color.put(stmt.arg1, colStr(curCol));
			}
			if (stmt.arg2 != null && stmt.arg2.startsWith("r") && reg2Color.get(stmt.arg2) == null) {
				curCol = defColor(curCol);
				reg2Color.put(stmt.arg2, colStr(curCol));
			}
		}

		String dotG = "digraph {\nnode [shape=box, labeljust=l]\n";
		for (int i = 0; i < stmts.size(); i++) {
			AsmStmt stmt = stmts.get(i);
			dotG += String.format("%s [label= " + nodeTemplate(stmt, reg2Color) + "];%n", i, stmt.toString());
			for (int j = 0; j < N; j++) {
				if (g[i][j] == 1) {
					dotG += String.format("%s -> %s;%n", i, j);
				}
			}
		}
		dotG += "}";
		
		Files.write(dir.resolve("g.dot"), dotG.getBytes());

		ProcessBuilder processBuilder = new ProcessBuilder("dot -Tsvg -og.svg g.dot".split(" "));
		processBuilder.directory(dir.toFile());
		Process process = processBuilder.start();
		int exitCode = process.waitFor();
		System.out.println("exitCode = " + exitCode);
		ByteStreams.copy(process.getErrorStream(), System.err);
		ByteStreams.copy(process.getInputStream(), System.err);

		if (exitCode == 0)
			Desktop.getDesktop().browse(dir.resolve("g.svg").toUri());

	}

	private static String unify(String arg) {
		if (arg == null) return null;

		switch (arg) {
			case "r26":
				return "X";
			case "r28":
				return "Y";
			case "r30":
				return "Z";
			default:
				return arg;
		}
	}

	private static String colStr(int[] curCol) {
		int i = 0;
		return String.format("#%2X%2X%2X", 255 / curCol[i++], 255 / curCol[i++], 255 / curCol[i++]);
	}

	private static int[] defColor(int[] curCol) {
		return add1(curCol, 0, 3);
	}

	private static int[] add1(int[] curCol, int i, int limit) {
		if (i == curCol.length) throw new AssertionError();

		if (curCol[i] == limit) {
			curCol[i] = 1;
			return add1(curCol, i + 1, limit);
		} else {
			curCol[i]++;
			return curCol;
		}
	}

	private static String nodeTemplate(AsmStmt stmt, Map<String, String> reg2Color) {
		return String.format("<<table border=\"0\" cellspacing=\"0\" cellborder=\"0\">\n" +
				"                          <tr>\n" +
				"                              <td>%4x : %s <font color=\"%s\">%s</font>%s<font color=\"%s\">%s</font></td>\n" +
				"                          </tr>\n" +
				"                       </table> \n" +
				"                      >",
				stmt.adr, stmt.cmd, reg2Color.getOrDefault(stmt.arg1, "#000000"), stmt.arg1,
				(stmt.arg2 == null ? "": ", "), reg2Color.getOrDefault(stmt.arg2, "#000000"), (stmt.arg2 == null ? " ": stmt.arg2));
	}

	private static void parseAsmStmt(List<AsmStmt> stmts, String line) {
		List<String> parts = Splitter.onPattern("\\s+").trimResults().omitEmptyStrings().splitToList(line);
		int adr = Integer.parseInt(parts.get(0), 16);
		if (parts.size() == 5) {
			String firstArg = parts.get(3);
			stmts.add(new AsmStmt(adr, parts.get(2), firstArg.substring(0, firstArg.length() - 1), parts.get(4)));
		} else if (parts.size() == 4) {
			stmts.add(new AsmStmt(adr, parts.get(2), parts.get(3), null));
		} else {
			stmts.add(new AsmStmt(adr, parts.get(2), null, null));
		}
	}

	private static class AsmStmt {
		int adr;
		String cmd;
		String arg1;
		String arg2;

		public AsmStmt(int adr, String cmd, String arg1, String arg2) {
			this.adr = adr;
			this.cmd = cmd;
			this.arg1 = arg1;
			this.arg2 = arg2;
		}

		@Override
		public String toString() {
			return "AsmStmt{" +
					"adr=" + Integer.toHexString(adr) +
					", cmd='" + cmd + '\'' +
					", arg1='" + arg1 + '\'' +
					", arg2='" + arg2 + '\'' +
					'}';
		}
	}
}
