/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.buijs.klutter.plugins.gradle.dsl

import dev.buijs.klutter.core.KlutterConfigException


/**
 * @author Gillian Buijs
 */
@DslMarker
internal annotation class RepositoriesDSLMarker

@RepositoriesDSLMarker
class KlutterRepositoriesDSL: KlutterDSL<KlutterRepositoriesBuilder> {
    override fun configure(lambda: KlutterRepositoriesBuilder.() -> Unit): KlutterRepositoriesDTO {
        return KlutterRepositoriesBuilder().apply(lambda).build()
    }
}

@RepositoriesDSLMarker
class KlutterRepositoriesBuilder: KlutterDSLBuilder {

    private val repositories = mutableListOf<KlutterRepository>()

    fun maven(lambda: MavenBuilder.() -> Unit) {
        MavenBuilder().apply(lambda).build().also {
            repositories.add(it)
        }
    }

    override fun build() = KlutterRepositoriesDTO(repositories)

}

@DslMarker
internal annotation class MavenRepositoryDSLMarker

@MavenRepositoryDSLMarker
class MavenBuilder {

    var url: String? = null
    var username: String? = null
    var password: String? = null

    fun build(): KlutterRepository {

        if(url == null) {
            throw KlutterConfigException("Can not configure a maven repository without URL")
        }

        if(username == null && password == null) {
            return KlutterRepository(url = url!!)
        }

        if(username != null && password != null){
            return KlutterRepository(
                url = url!!,
                username = username,
                password = password
            )
        }

        throw KlutterConfigException(
            "Can not configure a maven repository with incomplete credentials. Supply both an username and password.")

    }

    fun secret(key: String): String {
        //todo when encryption and config separation is complete then this function should look at the correct file/env var/whatever
        return key
    }

    fun local(key: String): String {
        //todo same same as secret without encryption
        return key
    }

    fun shared(key: String): String {
        //todo same same same as local but could look at the current file
        return key
    }

}

data class KlutterRepositoriesDTO(val repositories: List<KlutterRepository>): KlutterDTO

data class KlutterRepository(
    val type: KlutterRepositoryType = KlutterRepositoryType.MAVEN,
    val url: String,
    val username: String? = null,
    val password: String? = null,
)

enum class KlutterRepositoryType {
    MAVEN
}