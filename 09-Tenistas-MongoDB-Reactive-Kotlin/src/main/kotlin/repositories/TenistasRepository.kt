package repositories

import models.Tenista
import org.litote.kmongo.Id

interface TenistasRepository : CrudRepository<Tenista, Id<Tenista>>