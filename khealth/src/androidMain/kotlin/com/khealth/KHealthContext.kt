/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

internal object KHealthContext {
    private var applicationContext: Context? = null

    fun set(context: Context) {
        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }
    }

    fun get(): Context {
        return applicationContext ?: error(
            "KHealth application context has not been initialized. " +
                    "Please ensure KHealthInitProvider is registered in AndroidManifest or call " +
                    "KHealth(context) directly."
        )
    }

    val isInitialized: Boolean
        get() = applicationContext != null
}

/**
 * Automatically initializes [KHealthContext] with the application context upon app launch.
 */
class KHealthInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let { KHealthContext.set(it) }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
