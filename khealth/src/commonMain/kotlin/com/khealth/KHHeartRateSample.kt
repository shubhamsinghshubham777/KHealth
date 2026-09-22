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

import kotlin.time.Instant

/**
 * Represents a single measurement of the heart rate ([KHRecord.HeartRate]).
 *
 * @param time The point in time when the measurement was taken.
 * // TODO: Ensure this validation range is respected on all platforms
 * @param beatsPerMinute Heart beats per minute. Validation range: 1-300.
 */
data class KHHeartRateSample(val beatsPerMinute: Long, val time: Instant)
