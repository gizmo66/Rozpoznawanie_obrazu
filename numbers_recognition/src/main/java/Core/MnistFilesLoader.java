package Core;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

class MnistFilesLoader implements FileLoader {

    private static final int OFFSET_SIZE = 4;
    private static final int NUMBER_ITEMS_OFFSET = 4;
    private static final int ITEMS_SIZE = 4;
    private static final int ROWS = 28;
    private static final int COLUMNS = 28;
    private static final int IMAGE_OFFSET = 16;
    private static final int IMAGE_SIZE = ROWS * COLUMNS;

    List<Picture> loadTrainingDataSet(File file) throws IOException {
        List<Picture> pictures = new ArrayList<>();

        String imagesFileName = file.getName();
        String imagesFileNamePrefix = imagesFileName.substring(0, imagesFileName.indexOf("-"));
        File labelFile = new File(file.getPath().replace(imagesFileName, "")
                + imagesFileNamePrefix + "-labels.idx1-ubyte");

        ByteArrayOutputStream labelBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();

        FileInputStream imageInputStream = new FileInputStream(file);
        FileInputStream labelInputStream = new FileInputStream(labelFile);

        int read;
        byte[] buffer = new byte[16384];

        while((read = labelInputStream.read(buffer, 0, buffer.length)) != -1) {
            labelBuffer.write(buffer, 0, read);
        }

        labelBuffer.flush();

        while((read = imageInputStream.read(buffer, 0, buffer.length)) != -1) {
            imageBuffer.write(buffer, 0, read);
        }

        imageBuffer.flush();

        byte[] labelBytes = labelBuffer.toByteArray();
        byte[] imageBytes = imageBuffer.toByteArray();

        int numberOfLabels = ByteBuffer.wrap(Arrays.copyOfRange(labelBytes, NUMBER_ITEMS_OFFSET,
                NUMBER_ITEMS_OFFSET + ITEMS_SIZE)).getInt();
        int numberOfImages = ByteBuffer.wrap(Arrays.copyOfRange(imageBytes, NUMBER_ITEMS_OFFSET,
                NUMBER_ITEMS_OFFSET + ITEMS_SIZE)).getInt();

        if(numberOfImages != numberOfLabels) {
            throw new IOException("The number of labels and images do not match!");
        }

        for(int i = 0; i < numberOfLabels; i++) {
            int label = labelBytes[OFFSET_SIZE + ITEMS_SIZE + i];
            byte[] imageData = Arrays.copyOfRange(imageBytes, (i * IMAGE_SIZE) + IMAGE_OFFSET,
                    (i * IMAGE_SIZE) + IMAGE_OFFSET + IMAGE_SIZE);

            pictures.add(new Picture(ImageUtils.bytesToImage(imageData), String.valueOf(label)));
        }

        return pictures;
    }
}
