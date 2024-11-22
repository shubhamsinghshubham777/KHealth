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

import co.touchlab.kermit.Logger

internal fun logDebug(message: String, methodName: String? = null) {
    Logger.d(
        tag = "KHealth - $methodName",
        throwable = null,
        messageString = "[KHealth] ${methodName?.let { "($it)" }} -> $message"
    )
}

internal fun logError(throwable: Throwable? = null, methodName: String, message: String? = null) {
    Logger.e(
        tag = "KHealth - $methodName",
        throwable = throwable,
        messageString = (message ?: throwable?.message).orEmpty()
    )
}
