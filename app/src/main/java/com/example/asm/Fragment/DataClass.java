package com.example.asm.Fragment;


    public class DataClass {
        private String dataId;
        private String dataTitle;
        private String dataDesc;
        private String dataGia;
        private String dataImage;

        public DataClass() {
        }

        public DataClass(String dataId, String dataTitle, String dataDesc, String dataGia, String dataImage) {
            this.dataId = dataId;
            this.dataTitle = dataTitle;
            this.dataDesc = dataDesc;
            this.dataGia = dataGia;
            this.dataImage = dataImage;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getDataTitle() {
            return dataTitle;
        }

        public void setDataTitle(String dataTitle) {
            this.dataTitle = dataTitle;
        }

        public String getDataDesc() {
            return dataDesc;
        }

        public void setDataDesc(String dataDesc) {
            this.dataDesc = dataDesc;
        }

        public String getDataGia() {
            return dataGia;
        }

        public void setDataGia(String dataGia) {
            this.dataGia = dataGia;
        }

        public String getDataImage() {
            return dataImage;
        }

        public void setDataImage(String dataImage) {
            this.dataImage = dataImage;
        }
    }
