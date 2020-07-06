package somind.dtlab.models

import java.time.ZonedDateTime

final case class DtType(
    // the name of our type
    name: String,
    // the names of the properties (called props instead of attributes because
    // values of props can change - values of attributes cannot change)
    props: List[String],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// todo: self-ref data structure for messaging actor tree (and support graph)
final case class Update()
