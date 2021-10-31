package ru.job4j.pooh;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        VerifyContent verifyContent = new VerifyContent(content).verify();
        String httpRequestType = verifyContent.getHttpRequestType();
        String poohMode = verifyContent.getPoohMode();
        String sourceName = verifyContent.getSourceName();
        String param = verifyContent.getParam();
        return new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

    private static class VerifyContent {
        private String content;
        private String httpRequestType;
        private String poohMode;
        private String sourceName;
        private String param;

        public VerifyContent(String content) {
            this.content = content;
        }

        public String getHttpRequestType() {
            return httpRequestType;
        }

        public String getPoohMode() {
            return poohMode;
        }

        public String getSourceName() {
            return sourceName;
        }

        public String getParam() {
            return param;
        }

        public VerifyContent verify() {
            String[] lines = content.split("\r\n");
            if (lines.length < 2) {
                throw new IllegalArgumentException();
            }
            String[] firstLine = lines[0].split(" ");
            if (firstLine.length < 2) {
                throw new IllegalArgumentException();
            }
            String[] mode = firstLine[1].split("/");
            if (mode.length < 3) {
                throw new IllegalArgumentException();
            }
            httpRequestType = firstLine[0];
            if (!"GET".equalsIgnoreCase(httpRequestType)
                    && !"POST".equalsIgnoreCase(httpRequestType)) {
                throw new IllegalArgumentException();
            }
            poohMode = mode[1];
            sourceName = mode[2];
            param = "";
            if ("QUEUE".equalsIgnoreCase(poohMode)) {
                if ("GET".equalsIgnoreCase(httpRequestType)) {
                    param = "";
                } else if ("POST".equalsIgnoreCase(httpRequestType)) {
                    param = lines[lines.length - 1];
                    if (!param.contains("=")) {
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            } else if ("TOPIC".equalsIgnoreCase(poohMode)) {
                if ("GET".equalsIgnoreCase(httpRequestType)) {
                    if (mode.length < 4) {
                        throw new IllegalArgumentException();
                    }
                    param = mode[3];
                } else if ("POST".equalsIgnoreCase(httpRequestType)) {
                    param = lines[lines.length - 1];
                    if (!param.contains("=")) {
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalArgumentException();
            }
            return this;
        }
    }
}