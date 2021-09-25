package render

import kotlin.time.ExperimentalTime

//typealias Slots = MutableMap<Int, View>

//typealias Queues = MutableMap<Int, Slots>

@ExperimentalTime
@ExperimentalUnsignedTypes
object RenderActionPlex {

/*
    @ExperimentalUnsignedTypes
    val instances: RenderInstancePositionMap = mutableMapOf()

    val renderHeaders : Slots = mutableMapOf()

    val renderSlotsBg : Queues = mutableMapOf()

    val renderSlotsFg : Queues = mutableMapOf()

    val renderSlotsText : Queues = mutableMapOf()

    val fancyPaint = LinearGradientPaint(0, 0, 0, 50).add(0.0, Colors.CADETBLUE).add(1.0, Colors.PURPLE)

    val fancyFont = BitmapFont(DefaultTtfFont, 64.0, paint = fancyPaint)

    val descriptionTextSlots : Slots = mutableMapOf()

    val descriptions : MutableMap<Int, String> = mutableMapOf()

    var descriptionSlot = 0

    //TODO: replace with Korge-friendly dispatcher / context
    fun getDispatcher() = Dispatchers.Default //used for flowOn()
    fun getCoroutineContext() = CoroutineScope(getDispatcher()).coroutineContext //used for launch() //views().coroutineContext ?

    fun lateInit(container: Container) = runBlockingNoSuspensions {

        val startingPosition = Point(10, 50)

        val xOffset = 150
        val yOffset = 300

        //fill 12 text headers
        (0..11).toList().forEach { headerIdx -> renderHeaders.put(headerIdx, container.text(text = "init",
                font = fancyFont, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT
            ).position(startingPosition.x + (headerIdx % 6) * xOffset, startingPosition.y + (headerIdx / 6) * yOffset))
        }

        //fill 12 background slot rects
        (0..11).toList().forEach { queueIdx ->
            renderSlotsBg[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.roundRect(80, 20, 1, 1, fill = Colors["#727170"].withA(80))
                    .position(startingPosition.x + (queueIdx % 6) * xOffset, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
            }.toMutableMap()
        }

        //fill 12 foreground slot rects
        (0..11).toList().forEach { queueIdx ->
            renderSlotsFg[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.roundRect(80, 20, 1, 1, fill = Colors["#727170"])
                    .position(startingPosition.x + (queueIdx % 6) * xOffset, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
            }.toMutableMap()
        }

        //fill 12 slot texts
        (0..11).toList().forEach { queueIdx ->
            renderSlotsText[queueIdx] =
                (0..7).toList().associate {slotIdx -> let {slotIdx to container.text("init", textSize = 14.0, color = Colors["#b9c3ff"])//Colors["#3e3e3e"])
                    .position(startingPosition.x + (queueIdx % 6) * xOffset + 10, startingPosition.y + (queueIdx / 6) * yOffset + (slotIdx + 1) * 25)
                }
            }.toMutableMap()
        }

        //fill 12 exec description text slots
        (0..11).toList().forEach { headerIdx -> descriptionTextSlots.put(headerIdx, container.text(text = "description",
            font = fancyFont, textSize = 18.0, alignment = TextAlignment.BASELINE_LEFT
            ).position(startingPosition.x +10, startingPosition.y + 2 * yOffset + 25 * headerIdx))
        }

        //fill 12 exec description text slots
        (0..11).toList().forEach { headerIdx -> descriptions.put(headerIdx, "description") }

        //guide boxes
        container.text(text = "instance", font = fancyFont, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT
        ).position(startingPosition.x + 6 * xOffset, startingPosition.y)

        var slotIdx = 0

        container.roundRect(80, 20, 1, 1, fill = Colors["#727170"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("queued", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#00b600"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("prepared", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#100be0"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("executed", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#e00508"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("recovered", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#727170"].withA(180))
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.roundRect(40, 20, 1, 1, fill = Colors["#727170"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("queueing", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#00b600"].withA(180))
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.roundRect(40, 20, 1, 1, fill = Colors["#00b600"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("preparing", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#100be0"].withA(180))
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.roundRect(40, 20, 1, 1, fill = Colors["#100be0"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("executing", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        slotIdx++

        container.roundRect(80, 20, 1, 1, fill = Colors["#e00508"].withA(180))
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.roundRect(40, 20, 1, 1, fill = Colors["#e00508"])
            .position(startingPosition.x + 6 * xOffset, startingPosition.y + (slotIdx + 1) * 25)
        container.text("recovering", textSize = 14.0, color = Colors["#b9c3ff"])
            .position(startingPosition.x + 6 * xOffset + 10, startingPosition.y + (slotIdx + 1) * 25)

        // reset queue boxes for run
        clearQueues()
        clearDescriptions()

        val meshesHeight = 7
        val meshPosition = Point(512, 612)
        val roomMesh = INodeMesh.buildRoomMesh(centerPoint = meshPosition, height = meshesHeight)

        val rooms = roomMesh.nodes.size / 8
        val maxIter = roomMesh.nodes.size / 6

        val roomClusters = roomMesh.getClusters(rooms = rooms, maxIterations = maxIter)

        val renderPoints =
            if (roomClusters.isNotEmpty()) roomClusters.values.flatten() else roomMesh.nodes

        //render nodeMesh rooms based upon scaledMesh centroids
        container.graphics {
            stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (meshLine in roomMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }
        }

        for (meshNode in renderPoints) {
            //https://stackoverflow.com/questions/5320525/regular-expression-to-match-last-number-in-a-string
            val numberRegex = Regex("(\\d+)(?!.*\\d)")

            val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

            container.circle {
                position(meshNode.position)
                radius = 5.0
                color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                strokeThickness = 3.0
                onClick {
//                    commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
//                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
//                    commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                }
            }
        }
    }

    fun clearQueues() {
        (0..11).toList().forEach { queueIdx -> clearQueue(queueIdx) }
    }

    fun clearQueue(queueIdx : Int) {

        renderHeaders[queueIdx]!!.colorMul = Colors.BLACK
        renderHeaders[queueIdx]!!.setText("empty")
        (0..7).toList().forEach { slotIdx ->
            clearSlot(queueIdx, slotIdx)
        }
    }

    fun clearSlot(queueIdx : Int, slotIdx : Int) {

        renderSlotsBg[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsFg[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsText[queueIdx]!![slotIdx]!!.colorMul = Colors.BLACK
        renderSlotsText[queueIdx]!![slotIdx]!!.setText("empty")
    }

    fun clearDescriptions() {

        (0..11).toList().forEach { slotIdx -> clearDescription(slotIdx) }
    }

    fun clearDescription(slotIdx : Int) {

        descriptions[slotIdx] = ""
        descriptionTextSlots[slotIdx]!!.colorMul = Colors.BLACK
        descriptionTextSlots[slotIdx]!!.setText(descriptions[slotIdx]!!)
    }

    fun renderQueue(queueIdx : Int, instanceName : String, instanceMoment : Moment, actionPlexMap: Map<UUID, StateAction>, interrupted: Boolean) {

        renderHeaders[queueIdx]!!.colorMul = Colors.CADETBLUE
        renderHeaders[queueIdx]!!.setText(instanceName)

        var slotIdx = 0

     //   println("rendering instance: $instanceName")

        actionPlexMap.forEach { slot ->
            (1..slot.value.plexSlotsFilled).toList().forEach {
                if (slotIdx <= 7) renderSlot(queueIdx, slotIdx, instanceMoment, slot.value, interrupted)
 //               println(slot.value)
                slotIdx++
            }
        }

  //      println("slots rendered : $slotIdx")

   //     println("slots remaining to clear: ${7-slotIdx}")

        //clean up remaining slots
        (slotIdx..7).toList().forEach { clearIdx ->
            clearSlot(queueIdx, clearIdx)
        }
    }

    fun renderSlot(queueIdx : Int, slotIdx : Int, instanceMoment : Moment, stateAction : StateAction, interrupted: Boolean) {

        val momentsElapsed = (stateAction.timer.getMillisecondsElapsed() / instanceMoment.milliseconds).toDouble()

        val percentFilled = when (stateAction.actionState) {
            ActionPrepare -> (momentsElapsed + 1) / stateAction.action.momentsToPrepare.toDouble()
            ActionExecute -> (momentsElapsed + 1) / stateAction.action.momentsToExecute.toDouble()
            ActionRecover -> (momentsElapsed + 1) / stateAction.action.momentsToRecover.toDouble()
            else -> momentsElapsed / (momentsElapsed + 1) //Zeno's queue
        }

//        if (percentFilled > 1) println("percentFilled overflow : $percentFilled for moment ${instanceMoment.milliseconds} $stateAction")

        val renderPercentFilled = if (percentFilled > 1) 1.0 else percentFilled

        //println ("momentsElapsed: $momentsElapsed -> percentFilled: $percentFilled")

        val fillText = stateAction.action.actionLabel

//        println("interrupted: $interrupted, stateAction.actionPriority == BaseAction: ${stateAction.actionPriority == BaseAction}")

        val fillTextColor = when (interrupted) {
            true -> Colors["#171717"]
            false -> Colors["#b9c3ff"]
        }

        val fillColor = when (interrupted) {
            true -> Colors["#eaeaea"]
            false -> when (stateAction.actionPriority == BaseAction) {
                true -> when (stateAction.actionState) {
                    ActionPrepare -> Colors["#006c00"]
                    ActionExecute -> Colors["#080a6c"]
                    ActionRecover -> Colors["#6c0604"]
                    else -> Colors["#434241"]
                }
                false -> when (stateAction.actionState) {
                    ActionPrepare -> Colors["#00b600"]
                    ActionExecute -> Colors["#100be0"]
                    ActionRecover -> Colors["#e00508"]
                    else -> Colors["#727170"]
                }
            }
        }

//        println("stateAction.actionState: ${stateAction.actionState}, fillTextColor: $fillTextColor, fillColor: $fillColor")

        renderSlotsBg[queueIdx]!![slotIdx]!!.colorMul = fillColor.withA(180)
        renderSlotsFg[queueIdx]!![slotIdx]!!.colorMul = fillColor
        renderSlotsFg[queueIdx]!![slotIdx]!!.scaledWidth = 80.0 * renderPercentFilled
        renderSlotsText[queueIdx]!![slotIdx]!!.colorMul = fillTextColor
        renderSlotsText[queueIdx]!![slotIdx]!!.setText(fillText)
    }

    @ExperimentalUnsignedTypes
    fun getOpenPosition(): Int {
        var curPosIdx = 0
        val sizeIdx = instances.size - 1

        while (curPosIdx <= sizeIdx) {
            if (instances[curPosIdx] == null) return curPosIdx
            curPosIdx++
        }
        return instances.size
    }


    fun addInstance(kInstance: IInstance) {

        instances.put(getOpenPosition(), kInstance)

    }

    @ExperimentalUnsignedTypes
    fun removeInstance(kInstance: IInstance) {

        val instanceQueue = instances.filterValues { it == kInstance }.keys.toList()[0]

        clearQueue(instanceQueue)

        instances.remove(instanceQueue)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun render(instanceId : UUID, instanceMoment: Moment, actionPlexMap: Map<UUID, StateAction>, interrupted : Boolean = false) = coroutineScope {

     //   val checkTimer = Timer()

     //   val instanceViews: RenderInstanceViewMap = mutableMapOf()

        val renderInstanceEntry : Map.Entry<Int, IInstance>? = instances.filterValues { it.getInstanceId() == instanceId }.entries.firstOrNull()

        if (renderInstanceEntry == null) {
            println("RenderActionPlex.perform() instance not found for uuid $instanceId")
            return@coroutineScope //Timer()
        }

        renderQueue(
            queueIdx = renderInstanceEntry.key,
            instanceName = renderInstanceEntry.value.getInstanceName(),
            instanceMoment = instanceMoment,
            actionPlexMap = actionPlexMap,
            interrupted = interrupted
        )

        return@coroutineScope
    }

    fun scrollUp() {

        (1..11).toList().forEach { descriptionSlotIdx ->
            descriptions[descriptionSlotIdx - 1] = descriptions[descriptionSlotIdx]!!
        }

        descriptions[11] = ""
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun renderDescription(renderText: String) = coroutineScope {

   //     val checkTimer = Timer()

  //      println("renderText: $renderText @ slot: $descriptionSlot")

        descriptions[descriptionSlot++] = renderText

        if (descriptionSlot > 11) {
            descriptionSlot = 11
            scrollUp()
        }

        if (descriptionSlot < 11) {
            descriptionTextSlots[descriptionSlot]!!.colorMul = Colors["#b9c3ff"]
        }

        (0..descriptionSlot).toList().forEach { descriptionSlotIdx ->
            descriptionTextSlots[descriptionSlotIdx].setText(descriptions[descriptionSlotIdx]!!)
        }

        return@coroutineScope
    }
*/
}
