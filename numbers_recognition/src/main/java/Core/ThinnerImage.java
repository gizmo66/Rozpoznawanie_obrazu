package Core;
import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ThinnerImage {

    final static String[] image = {
            "                                                          ",
            " #################                   #############        ",
            " ##################               ################        ",
            " ###################            ##################        ",
            " ########     #######          ###################        ",
            "   ######     #######         #######       ######        ",
            "   ######     #######        #######                      ",
            "   #################         #######                      ",
            "   ################          #######                      ",
            "   #################         #######                      ",
            "   ######     #######        #######                      ",
            "   ######     #######        #######                      ",
            "   ######     #######         #######       ######        ",
            " ########     #######          ###################        ",
            " ########     ####### ######    ################## ###### ",
            " ########     ####### ######      ################ ###### ",
            " ########     ####### ######         ############# ###### ",
            "                                                          "};

    final static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
            {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    final static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6},
            {0, 4, 6}}};

    static List<Point> toWhite = new ArrayList<>();
    static int[][] grid;

    public static Image Start(Picture p) {
        BufferedImage p1 = ImageUtils.toBufferedImage(p.getImage());
        grid = new int[28][];
        int index = 0;
        for(int i = 0 ;i < 28; i ++)
        {
            grid[index] = getOneRow(p1,i);
            index++;
        }
        //for (int r = 0; r < image.length; r++)
        //    grid[r] = image[r].toCharArray();
        //printResult();
        thinImage();

        return ImageUtils.toBufferImageFrom2DArray(grid);
    }

    static int[] getOneRow(BufferedImage img, int rowIndex)
    {
        int[] temp = new int[28];
        for(int i = 0 ; i< 28; i++)
        {
            if(img.getRGB(i,rowIndex) == Color.BLACK.getRGB())
                temp[i] = 1;//img.getRGB(i,rowIndex);
            else if(img.getRGB(i,rowIndex) == Color.WHITE.getRGB())
                temp[i] = 0;
        }

        return temp;
    }

    static void thinImage() {
        boolean firstStep = false;
        boolean hasChanged;

        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int r = 1; r < grid.length - 1; r++) {
                for (int c = 1; c < grid[0].length - 1; c++) {

                    if (grid[r][c] != 1)
                        continue;

                    int nn = numNeighbors(r, c);
                    if (nn < 2 || nn > 6)
                        continue;

                    if (numTransitions(r, c) != 1)
                        continue;

                    if (!atLeastOneIsWhite(r, c, firstStep ? 0 : 1))
                        continue;

                    toWhite.add(new Point(c, r));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite)
                grid[p.y][p.x] = 0;
            toWhite.clear();

        } while (firstStep || hasChanged);

        //printResult();
    }

    static int numNeighbors(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == 1)
                count++;
        return count;
    }

    static int numTransitions(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == 0) {
                if (grid[r + nbrs[i + 1][1]][c + nbrs[i + 1][0]] == 1)
                    count++;
            }
        return count;
    }

    static boolean atLeastOneIsWhite(int r, int c, int step) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (grid[r + nbr[1]][c + nbr[0]] == 0) {
                    count++;
                    break;
                }
            }
        return count > 1;
    }

    static void printResult() {
        for (int[] row : grid) {
            for (int i = 0; i < row.length; i++) {
                System.out.print(row[i]);
            }
            System.out.println("");
        }
    }
}