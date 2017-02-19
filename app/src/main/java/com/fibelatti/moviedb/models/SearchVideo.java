
package com.fibelatti.moviedb.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchVideo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<SearchVideoResult> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<SearchVideoResult> getResults() {
        return results;
    }

    public void setResults(List<SearchVideoResult> results) {
        this.results = results;
    }

}
