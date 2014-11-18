/* 
 * @(#)BookInfo.java               Project:bookscan 
 * Date:2012-12-3 
 * 
 * Copyright (c) 2011 CFuture09, Institute of Software,  
 * Guangdong Ocean University, Zhanjiang, GuangDong, China. 
 * All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */  
package com.digdream.tasbook.bean;  

/** 
 * @author Geek_Soledad (66704238@51uc.com) 
 */  
public class Book {  

    public String isbn10;// 10位的ISBN  
    public String isbn13;// 13位的ISBN  
    public String name; // 书名  
    public String author;// 作者名  
    public String summary;// 简介  
    public String publisher; // 出版社  
    public String image; // 封面图片地址  
	public String _id;
	public String classify;
	public String getClassify(){
		return classify;
	}
	public void setClassify(String classify){
		this.classify = classify;
	}
	public String get_id(){
		return _id;
	}
	public void set_id(String _id){
		this._id = _id;
	}

    public String getIsbn10() {  
        return isbn10;  
    }  

    public void setIsbn10(String isbn10) {  
        this.isbn10 = isbn10;  
    }  

    public String getIsbn13() {  
        return isbn13;  
    }  

    public void setIsbn13(String isbn13) {  
        this.isbn13 = isbn13;  
    }  

    public String getName() {  
        return name;  
    }  

    public void setName(String name) {  
        this.name = name;  
    }  

    public String getAuthor() {  
        return author;  
    }  

    public void setAuthor(String author) {  
        this.author = author;  
    }  

    public String getSummary() {  
        return summary;  
    }  

    public void setSummary(String summary) {  
        this.summary = summary;  
    }  

    public String getPublisher() {  
        return publisher;  
    }  

    public void setPublisher(String publisher) {  
        this.publisher = publisher;  
    }  

    public String getImage() {  
        return image;  
    }  

    public void setImage(String image) {  
        this.image = image;  
    }  

    @Override  
    public String toString() {  
        return "Book [isbn10=" + isbn10 + ", isbn13=" + isbn13 + ", name="  
                + name + ", author=" + author + ", summary=" + summary  
                + ", publisher=" + publisher + ", image=" + image + "]";  
    }  
}  