package repositories.tenistas

import models.Tenista
import repositories.CrudRepository

interface TenistasRepository : CrudRepository<Tenista, String>