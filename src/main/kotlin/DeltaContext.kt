class DeltaContext {

    private val equality: Boolean
        get() = mismatches.isEmpty()

    private val mismatches = mutableMapOf<String, MutableList<Mismatch>>()

    private val ignoredFields = mutableListOf<String>()

    fun areEquals() = equality

    fun addMismatch(field: String, mismatch: Mismatch?) {
        mismatch?.apply {
            if (mismatches[field] == null)
                mismatches[field] = mutableListOf()
            mismatches[field]!!.add(mismatch)
        }
    }

    fun getMismatches() = mismatches

    fun setIgnoredFields(vararg fields: String) {
        ignoredFields.addAll(fields)
    }

    fun getIgnoredFields() = ignoredFields
}
