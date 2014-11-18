package com.digdream.tasbook.database;

public final class TableColumns {
	public static class FilesColumns {
		public static final String COL_ID = "_id";
		/** 10位isbn */
		public static final String COL_ISBN10 = "isbn10";
		/** 13位isbn */
		public static final String COL_ISBN13 = "isbn13";
		/** 书名 */
		public static final String COL_NAME = "name";
		/** 作者名 */
		public static final String COL_AUTHOR = "author";
		/** 简介 */
		public static final String COL_SUMMARY = "summary";
		/** 出版社 */
		public static final String COL_PUBLISHER = "publisher";
		/** 图片缩略图 */
		public static final String COL_IMAGE = "image";
		/** 分类 */
		public static final String COL_CLASSIFY = "classify";

	}
}
