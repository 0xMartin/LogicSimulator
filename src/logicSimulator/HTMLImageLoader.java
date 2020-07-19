/* 
 * Copyright (C) 2020 Martin Krcma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logicSimulator;

/**
 *
 * @author Martin
 */
public class HTMLImageLoader {

    private static HTMLImageLoader hTMLImageLoader;

    private HTMLImageLoader() {
    }

    /**
     * Replace all image name marks by URI for java
     *
     * @param html HTML code
     * @param srcPath Path of source folder in project
     * @return
     */
    public String load(String html, String srcPath) {
        StringBuilder stringBuilder = new StringBuilder();

        String[] segments = html.split("_");

        for (String segment : segments) {
            if (segment.startsWith("{") && segment.endsWith("}")) {
                String imgName = segment.substring(1, segment.length() - 1);
                try {
                    stringBuilder.append(this.getClass().getResource(
                            srcPath + imgName.toLowerCase()).toURI().toString());
                } catch (Exception ex) {
                    ExceptionLogger.getInstance().logException(ex);
                }
            } else {
                stringBuilder.append(segment);
            }
        }

        return stringBuilder.toString();
    }

    public static HTMLImageLoader getInstance() {
        if (HTMLImageLoader.hTMLImageLoader == null) {
            HTMLImageLoader.hTMLImageLoader = new HTMLImageLoader();
        }
        return HTMLImageLoader.hTMLImageLoader;
    }

}
