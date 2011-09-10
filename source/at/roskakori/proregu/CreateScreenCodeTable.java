/**
 * Tool to create a HTML table of Commodore 64 screen codes.
 */
/*
Copyright (c) 2011, Thomas Aglassinger
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/
package at.roskakori.proregu;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.imageio.ImageIO;

public class CreateScreenCodeTable {
    private File targetFolder;
    private int zoom;
    private BufferedImage baseImage;
    private String htmlRows;

    public CreateScreenCodeTable() throws IOException {

        targetFolder = new File("site", "appendix-b");
        System.out.println("write screen codes to \"" + targetFolder.getAbsolutePath() + "\"");
        targetFolder.mkdirs();
        if (!targetFolder.exists() || !targetFolder.isDirectory()) {
            throw new FileNotFoundException("cannot find target folder: " + targetFolder);
        }
        baseImage = ImageIO.read(new File("classes", "c64-characters.png"));
        zoom = 3;
    }

    private BufferedImage getCharImage(int index, int column) {
        assert index >= 0;
        assert index < 128;
        assert (column == 0) || (column == 1);
        BufferedImage result = new BufferedImage(8 * zoom, 8 * zoom, BufferedImage.TYPE_BYTE_BINARY);
        int sourceX = index % 32;
        int sourceY = index / 32 + 4 * column;
        Graphics target = result.getGraphics();
        target.drawImage(baseImage, 0, 0, 8 * zoom, 8 * zoom, sourceX * 8, sourceY * 8, sourceX * 8 + 8,
                sourceY * 8 + 8, null);
        return result;
    }

    private File getCharImageFile(int index, int column) {
        assert (column == 0) || (column == 1);
        File result = new File(targetFolder, String.format("screen-code-%d-%03d.png", column, index));
        return result;
    }

    public void writeImage(int index, int column) throws IOException {
        assert (column == 0) || (column == 1);
        File targetFile = getCharImageFile(index, column);
        BufferedImage targetImage = getCharImage(index, column);
        ImageIO.write(targetImage, "png", targetFile);
    }

    private void writeRow(int index, int column) throws IOException {
        File targetFile = getCharImageFile(index, column);
        BufferedImage targetImage = getCharImage(index, column);
        ImageIO.write(targetImage, "png", targetFile);
        htmlRows += "<td><img src=\"" + targetFile.getName() + "\"></td>";
    }

    public void writeRow(int index) throws IOException {
        htmlRows += "<tr>";
        writeRow(index, 0);
        writeRow(index, 1);
        htmlRows += "<td>" + index + "</td>";
        htmlRows += "<tr>\n";
    }

    public void writeHtmlTable() throws IOException {
        htmlRows = "<table><tr><th>Set 1</th><th>Set 2</th><th>POKE</th></tr><tbody>\n";
        for (int index = 0; index < 128; index += 1) {
            writeRow(index);
        }
        htmlRows += "</tbody></table>";

        File htmlTableFile = new File(targetFolder, "appendix-b.html");
        Writer out = new FileWriter(htmlTableFile);
        try {
            out.write(htmlRows);
        } finally {
            out.close();
        }
    }

    public static void main(String[] args) throws Exception {
        CreateScreenCodeTable creator = new CreateScreenCodeTable();
        creator.writeHtmlTable();
    }
}
