/*
 *   Catalyst - AnvilPowered.org
 *   Copyright (C) 2020-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.catalyst.velocity

import com.google.inject.Injector
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.config.Registry
import org.anvilpowered.anvil.velocity.AnvilVelocityApi
import org.anvilpowered.anvil.velocity.createVelocity
import org.anvilpowered.catalyst.core.CatalystApi
import org.anvilpowered.catalyst.core.db.RepositoryScope

interface CatalystVelocityApi : CatalystApi {

    override val anvil: AnvilVelocityApi
}

fun CatalystApi.Companion.createVelocity(injector: Injector): CatalystVelocityApi {
    return object :
        CatalystVelocityApi,
        RepositoryScope by RepositoryScope.create() {
        override val anvil = AnvilApi.createVelocity(injector)
        override val registry: Registry
            get() = TODO("Not yet implemented")
    }
}
