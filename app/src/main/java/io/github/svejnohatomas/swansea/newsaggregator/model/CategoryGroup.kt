package io.github.svejnohatomas.swansea.newsaggregator.model

/**
 * A category of articles.
 *
 * @param name The name of the category.
 * @param articles The articles in the category.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class CategoryGroup(val name: String,
                    val articles: List<Article>)