import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class PathFinder {

    private static int size = -1;
    private char[][] grid;
    private static String fileName;
    private boolean[][] visited;
    private Nodes node;
    private final ArrayList<Nodes> store = new ArrayList<>();
    private boolean pathFound = false;

    public static PathFinder path = new PathFinder();

    public static void main(String[] args) {
        fileName = "map.txt";
        path.fileRead(fileName);
    }

    public void fileRead(String name) {
        try {
//            int blockCount = 0;
            InputStream stream = ClassLoader.getSystemResourceAsStream(name);
            assert stream != null;
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
            buffer.mark(1);

            String line;
            int row = 0;

            int rowLength = buffer.readLine().length();
            buffer.reset();

            grid = new char[rowLength][rowLength];
            visited = new boolean[grid.length][grid.length];

            while ((line = buffer.readLine()) != null) {
//                String[] rowValues = new String[line.length()];
                char[] columns = line.toCharArray();
                grid[row] = columns;

//                for (int i = 0; i < line.length(); i++) {
//                    rowValues[i] = String.valueOf(line.charAt(i));
//                    if (line.charAt(i) == '0') {
//                        blockCount++;
//                    }
//                }

//                // Lazy instantiation.
//                if (grid == null) {
//                    size = rowValues.length;
//                    grid = new char[size][size];
//                }

//                if (size >= 0) System.arraycopy(rowValues, 0, grid[row], 0, size);
                row++;
            }

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    System.out.print(grid[i][j]);
                }
                System.out.println();
            }
            findThePath();

//            int width = grid[0].length;
//            int height = grid[1].length;
//            int startI = 0;
//            int startJ = 0;
//            int endI = 0;
//            int endJ = 0;
////            int[][] blocks = new int[blockCount][2];
//
//            int r = 0;
//

//
//            for (int i = 0; i < grid[0].length; i++) {
//                for (int j = 0; j < grid[1].length; j++) {
//                    switch (grid[i][j].charAt(0)) {
//                        case 'S' -> {
//                            startI = i;
//                            startJ = j;
//                        }
//                        case 'F' -> {
//                            endI = i;
//                            endJ = j;
//                        }
////                        case '0' -> {
////                            blocks[r][0] = i;
////                            blocks[r][1] = j;
////                            r++;
////                        }
//                    }
//                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findThePath(){

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                // Finding source
                if (grid[i][j] == 'S') {
                    node = new Nodes(i, j);
//                    System.out.println("Start : " + (source.getColumnNumber()+1) +","+(source.getRowNumber()+1));
                }
            }
        }
        store.add(node);
        visited[node.getRowNumber()][node.getColumnNumber()] = true;
        Nodes itemVisited = null;

        while (!store.isEmpty()) {
            itemVisited = store.remove(0);
            int rowNumberVisited = itemVisited.getRowNumber();
            int columnNumberVisited = itemVisited.getColumnNumber();

            if (grid[rowNumberVisited][columnNumberVisited] == 'F') {
//                end = System.currentTimeMillis();
                pathFound = true;
                break;
            }

            move(itemVisited, -1, 0, "left");
            move(itemVisited, 1, 0, "right");
            move(itemVisited, 0, 1, "bottom");
            move(itemVisited, 0, -1, "up");
        }

        if (pathFound) {
            printPath(itemVisited);
        }
    }

    public void move(Nodes point, int x, int y, String direction) {
        int row = point.getRowNumber();
        int column = point.getColumnNumber();

        while(true) {
            row += y;
            column += x;

            if (!isValid(row, column)) {
                break;
            }

            if (grid[row][column] == 'F') {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.setPrevious(point);
                neighbourItem.setMove(direction);

                store.add(0, neighbourItem);
                visited[row][column] = true;
                break;
            }

            int nextRow  = row + y;
            int nextColumn = column + x;

            if ((nextRow < 0 || nextColumn < 0) || (nextRow >= grid.length || nextColumn >= grid.length) || (grid[nextRow][nextColumn] == '0')) {
                Nodes neighbourItem = new Nodes(row, column);
                neighbourItem.setPrevious(point);
                neighbourItem.setMove(direction);

                store.add(neighbourItem);
                visited[row][column] = true;
                break;
            }
        }
    }
    public boolean isValid(int row, int column) {
        if (row >= 0 && column >= 0
                && row < grid.length && column < grid[0].length
                && grid[row][column] != '0'
                && !visited[row][column]) {
            return true;
        }
        return false;
    }

    public void printPath(Nodes visited){
        ArrayList<String> path = new ArrayList<>();

        while (visited.getPrevious() != null) {
            String step = "Move " + visited.getMove() + " to " + "(" + (visited.getColumnNumber() + 1) + ", " + (visited.getRowNumber() + 1) + ")";
            path.add(step);
            visited = visited.getPrevious();
        }

        path.add("Start at " + "(" + (visited.getColumnNumber() + 1) + ", " + (visited.getRowNumber() + 1) + ")");

        int count = 1;

        Collections.reverse(path);

        String output = "";
        for (String step : path) {
            System.out.println(count + ". " + step);
            output += count + ". " + step + "\n";
            count += 1;
        }
        System.out.println(count + ". Done!");
        output += count + ". Done!\n\n";
    }
}
