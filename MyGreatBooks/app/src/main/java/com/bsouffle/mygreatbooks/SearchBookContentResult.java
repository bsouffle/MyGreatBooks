package com.bsouffle.mygreatbooks;

/**
 * Created by Benoit on 26/04/2014.
 */
final class SearchBookContentsResult {

    private final String isbn;
    private final String title;
    private final String author;
    private final String publisher;
    private final String description;

    private String imageUrl;
    private int pageCount = 0;
    private double averageRating = 0;
    private int ratingsCount = 0;

    SearchBookContentsResult(String isbn,
                             String title,
                             String author,
                             String publisher,
                             String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
    }

    SearchBookContentsResult(String isbn,
                             String title,
                             String author,
                             String publisher,
                             String description,
                             int pageCount,
                             double averageRating,
                             int ratingsCount) {
        this(isbn, title, author, publisher, description);

        setPageCount(pageCount);
        setAverageRating(averageRating);
        setRatingsCount(ratingsCount);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
