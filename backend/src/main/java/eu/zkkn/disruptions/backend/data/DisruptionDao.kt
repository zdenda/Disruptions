package eu.zkkn.disruptions.backend.data


class DisruptionDao : Dao() {

    fun save(disruption: Disruption): Long {
        return objectify.save().entity<Disruption>(disruption).now().id
    }

    fun load(guid: String): Disruption? {
        val list = objectify.load()
            .type<Disruption>(Disruption::class.java)
            .filter("guid", guid)
            .list()

        if (list.size > 0) return list[0]
        return null
    }

}
