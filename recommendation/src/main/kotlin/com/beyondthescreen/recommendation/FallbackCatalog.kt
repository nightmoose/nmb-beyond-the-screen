package com.beyondthescreen.recommendation

object FallbackCatalog {
    fun forTitle(title: Title): RecommendationSet {
        val curated = CURATED[title.id]
        if (curated != null) {
            return RecommendationSet(title.id, Source.CURATED, curated)
        }
        return generic(title)
    }

    val curatedIds: Set<String> = CURATED.keys

    private fun generic(title: Title): RecommendationSet {
        val topic = title.themes.firstOrNull() ?: title.name
        return RecommendationSet(
            titleId = title.id,
            source = Source.CURATED,
            categories = listOf(
                cat(
                    "books",
                    "Books",
                    s("A book on $topic", "Start with the subject this story keeps circling.", "$topic history book"),
                    s("Illustrated companion", "Pictures help when the film is really about a place or a craft.", "$topic illustrated book"),
                    s("Field notes", "A notebook is the smallest way to keep looking after the credits.", "hardcover notebook $topic"),
                ),
                cat(
                    "home",
                    "At home",
                    s("A print for the wall", "Keep one image of the world the story lives in.", "$topic art print"),
                    s("A lamp for reading", "Most of these bridges end in a chair and a book.", "reading lamp warm light"),
                    s("A mug for the evening", "The practical souvenir is something you use while you think about it.", "ceramic mug"),
                ),
                cat(
                    "sound",
                    "To listen to",
                    s("Instrumental album", "A record in the same mood, without asking the film for its score.", "$topic instrumental album"),
                    s("Headphones", "Close listening is the home version of a dark room.", "over ear headphones"),
                    s("A small speaker", "Put the mood in the room where you read the tie-in.", "bookshelf speaker"),
                ),
            ),
        )
    }
}

private val CURATED: Map<String, List<Category>> = mapOf(
    "master-and-commander" to listOf(
        cat(
            "books",
            "Naval history",
            s("Aubrey-Maturin novels", "The film follows O'Brian's captain and surgeon, and the novels carry the rest of their war.", "Patrick O'Brian Aubrey Maturin novels"),
            s("Nelson's navy", "Gunnery, rations, and rank here belong to the Royal Navy of the Napoleonic wars.", "Nelson navy history book"),
            s("Frigate design", "The wooden ship is the setting, and its build explains every cramped scene.", "age of sail frigate history book"),
        ),
        cat(
            "models",
            "Model ships",
            s("Wooden frigate kit", "Rigging and spars fill the frame, which is what a wooden model makes you build.", "wooden frigate ship model kit"),
            s("Rigging tools", "The film lingers on lines and blocks, the small hardware of a working ship.", "model ship rigging tools"),
            s("Ship plans", "A printed plan is the document the ship's carpenter would have trusted.", "historic sailing ship plans"),
        ),
        cat(
            "fashion",
            "Nautical dress",
            s("Wool pea coat", "The deck coats are heavy wool for spray and wind, and that cut left the ship.", "mens wool pea coat"),
            s("Linen shirt", "On watch or off, the crew lives in linen that can take salt water.", "linen band collar shirt"),
            s("Boat shoes", "Wet decks are why non-slip soles became everyday nautical style.", "leather boat shoes"),
        ),
        cat(
            "navigation",
            "Navigation",
            s("Brass sextant", "The chase only works if someone can still find the ship on an empty sea.", "brass sextant navigation"),
            s("Marine binoculars", "Lookouts spend the story on the horizon, which is a binoculars job.", "marine binoculars"),
            s("Parallel ruler", "Charts, not speeches, decide where the captain points the ship.", "nautical chart parallel ruler"),
        ),
        cat(
            "water",
            "On the water",
            s("Sailing gloves", "Half the work on a frigate is hands on rope, which sailors still protect.", "sailing gloves"),
            s("Navigation guide", "The plot is a navigation problem first and a battle second.", "coastal navigation study guide"),
            s("Sailing lesson", "The honest sequel is a few hours on a real boat.", "sailing lesson gift certificate"),
        ),
        cat(
            "music",
            "Below deck",
            s("Cello album", "The surgeon's cello is the quiet counterweight to the guns.", "classical cello album"),
            s("Chamber music", "Officers play together in the gunroom between watches.", "classical chamber music album"),
            s("Sea shanties", "The crew's songs are work songs for hauling, not a modern score.", "traditional sea shanties album"),
        ),
    ),
    "pride-and-prejudice" to listOf(
        cat(
            "books",
            "Books",
            s("Austen's novels", "The 1813 novel is public domain, and the other five books are the same voice.", "Jane Austen complete novels"),
            s("Regency life", "Rank, money, and visits are the machinery under the romance.", "Regency England social history book"),
            s("Letter writing", "The plot turns on what people commit to paper.", "history of letter writing book"),
        ),
        cat(
            "dress",
            "Dress",
            s("Empire-waist dress", "The silhouette of the rooms is the high waist and the long skirt.", "empire waist dress"),
            s("Day gloves", "A glove is a whole conversation in this world.", "cotton day gloves women"),
            s("Wool shawl", "Country walks and cold halls make the shawl a working garment.", "wool shawl wrap"),
        ),
        cat(
            "tea",
            "The tea table",
            s("Bone china cups", "Calls and confessions both happen over a cup.", "bone china teacup set"),
            s("Loose leaf tea", "The afternoon visit is a real meal, not a prop.", "loose leaf Earl Grey tea"),
            s("Scone mix", "Bread, jam, and tea are the edible version of the drawing room.", "scone baking mix"),
        ),
        cat(
            "hands",
            "Letters and thread",
            s("Wax seal kit", "A letter in this story is an object, not a message.", "wax seal stamp kit"),
            s("Embroidery kit", "Women's work in the film is needlework you can still sit down to.", "floral embroidery kit"),
            s("Writing desk", "A slope and a pen are the furniture of the plot.", "writing slope desk"),
        ),
    ),
    "interstellar" to listOf(
        cat(
            "books",
            "The science",
            s("The Science of Interstellar", "A physicist who advised the film wrote the companion to its rules.", "The Science of Interstellar book"),
            s("Popular astrophysics", "Time, gravity, and the farm are the same problem in plainer prose.", "astrophysics book for beginners"),
            s("Spaceflight history", "The ships are fiction, the engineering culture is not.", "human spaceflight history book"),
        ),
        cat(
            "sky",
            "Looking up",
            s("Beginner telescope", "The story starts by asking people to look past the dust.", "beginner telescope"),
            s("Star chart", "A paper chart is the low-tech version of their navigation.", "night sky star chart"),
            s("Planetarium projector", "A dark room and a ceiling of stars, without leaving the house.", "planetarium projector"),
        ),
        cat(
            "farm",
            "The farm",
            s("Heirloom corn seed", "The Earth they leave is a farm, and the crop is corn.", "heirloom corn seed"),
            s("Greenhouse kit", "Blight and dust make growing under cover the practical dream.", "hobby greenhouse kit"),
            s("Field journal", "The father and daughter keep time in notebooks.", "field journal notebook"),
        ),
        cat(
            "sound",
            "The room tone",
            s("Pipe organ album", "The score leans on a cathedral organ, and a pipe-organ record has that scale.", "pipe organ classical album"),
            s("Model rocket", "A small launch is the backyard version of leaving the ground.", "model rocket starter kit"),
            s("Blank watch", "A simple watch is the film's argument about time, worn on a wrist.", "minimal analog watch"),
        ),
    ),
    "whiplash" to listOf(
        cat(
            "practice",
            "Practice",
            s("Drum practice pad", "Most of the film is a pad, a stick, and a tempo that will not yield.", "drum practice pad"),
            s("Hickory sticks", "The tools are ordinary sticks, used until they are not.", "hickory drumsticks 5a"),
            s("Metronome", "The teacher's cruelty is a metronome with a voice.", "digital metronome"),
        ),
        cat(
            "listening",
            "Listening",
            s("Jazz trio album", "Small-band records are the tradition the student is trying to enter.", "jazz trio album"),
            s("Drumless tracks", "Play-along records are how a student practices with a band.", "jazz play along drumless"),
            s("Studio headphones", "He listens harder than he talks.", "closed back studio headphones"),
        ),
        cat(
            "books",
            "Reading",
            s("Jazz history", "The insults name a lineage. A history book is the calmer map.", "jazz history book"),
            s("Practice method", "The useful argument of the film is about deliberate repetition.", "deliberate practice music book"),
            s("Musician biography", "Drum stories are full of the same obsession, told by the players.", "jazz drummer biography"),
        ),
        cat(
            "room",
            "The room",
            s("Musician earplugs", "The volume in those rooms is a real injury risk.", "musician earplugs"),
            s("Music stand", "Charts on a stand are the furniture of the rehearsal room.", "sheet music stand"),
            s("Acoustic panels", "A practice room only works if the room stops fighting you.", "acoustic foam panels"),
        ),
    ),
    "the-grand-budapest-hotel" to listOf(
        cat(
            "pastry",
            "Pastry",
            s("Pastry cookbook", "The plot hides inside a pastry box. A real book is the edible version.", "European pastry cookbook"),
            s("Hot chocolate", "The hotel's comfort is a cup, a tray, and too much care.", "drinking chocolate set"),
            s("Cake stand", "Dessert in this story is presented like a state secret.", "ceramic cake stand"),
        ),
        cat(
            "travel",
            "Luggage",
            s("Carry-on trunk", "Everyone in the film is either arriving or fleeing with a case.", "vintage style suitcase"),
            s("Rail guide", "The republic is crossed by mountain trains.", "European train travel guide"),
            s("Toiletry case", "The concierge's power is partly a perfectly packed kit.", "leather toiletry case"),
        ),
        cat(
            "books",
            "Reading",
            s("Stefan Zweig", "The film's melancholy owes a public debt to Zweig's stories.", "Stefan Zweig collected stories"),
            s("Grand hotel history", "Lobby, staff, and guest form a whole society.", "historic grand hotel book"),
            s("Alpine photography", "The pink hotel sits in a real kind of mountain light.", "alpine hotel photography book"),
        ),
        cat(
            "lobby",
            "The lobby",
            s("Art deco clock", "Time in the hotel is a designed object.", "art deco desk clock"),
            s("Eau de cologne", "The concierge's day starts with scent and a pressed coat.", "eau de cologne classic"),
            s("Wool overcoat", "The travel coats are the costume that became ordinary winter wear.", "mens wool overcoat"),
        ),
    ),
    "jiro-dreams-of-sushi" to listOf(
        cat(
            "knives",
            "Knives",
            s("Yanagiba", "The long sushi knife is the tool the whole counter is built around.", "yanagiba sushi knife"),
            s("Whetstone", "Sharpening is shown as daily work, not a specialty.", "whetstone knife sharpening"),
            s("Knife skills book", "The gestures are teachable even if the ten seats are not.", "Japanese knife skills book"),
        ),
        cat(
            "pantry",
            "Pantry",
            s("Short-grain rice", "He treats the rice as the dish, and the fish as the guest.", "short grain sushi rice"),
            s("Nori", "Seaweed is one of the few ingredients the film lets you see plainly.", "sushi nori sheets"),
            s("Hangiri", "The wooden tub is how the rice is finished.", "hangiri sushi rice tub"),
        ),
        cat(
            "table",
            "The counter",
            s("Small plates", "The food arrives on quiet ceramics, one piece at a time.", "japanese ceramic small plates"),
            s("Chopsticks", "Even the sticks are part of the repetition.", "reusable chopsticks set"),
            s("Soy dish", "A small dish is the entire table setting.", "ceramic soy sauce dish"),
        ),
        cat(
            "books",
            "Reading",
            s("Sushi technique", "The film is a portrait. A manual is how the work continues.", "sushi technique cookbook"),
            s("Tokyo food guide", "The counter is in a city of other counters.", "Tokyo food guidebook"),
            s("Craft biography", "A life organized around one gesture is the actual subject.", "Japanese craft biography"),
        ),
    ),
    "sintel" to listOf(
        cat(
            "books",
            "Stories",
            s("Dragon folklore", "The dragon is an old story, not a mascot, and the folklore is the shelf for it.", "dragon folklore book"),
            s("Solo journey novel", "A traveler crossing bad weather is the shape of the short.", "wilderness journey novel"),
            s("Animation sketchbook", "Sintel is an open movie. A sketchbook is how that kind of film starts.", "character design sketchbook"),
        ),
        cat(
            "clothes",
            "On the road",
            s("Wool cloak", "The traveler lives in a heavy wrap against the snow.", "wool cloak cape"),
            s("Travel boots", "The road is the plot, and boots are the equipment.", "leather hiking boots"),
            s("Winter gloves", "Cold hands are the practical half of a mountain story.", "winter leather gloves"),
        ),
        cat(
            "outdoors",
            "Cold country",
            s("Winter camping guide", "Snow, stone, and a camp are the geography of the chase.", "winter camping guidebook"),
            s("Insulated bottle", "A hot drink is the small mercy in that weather.", "insulated steel bottle"),
            s("Mountain map", "The journey only makes sense if the land is real enough to fold.", "mountain trail map"),
        ),
        cat(
            "make",
            "Making it",
            s("Blender handbook", "The film was built in the open. The tool it was made with is the meta souvenir.", "Blender 3D beginner book"),
            s("Drawing pencils", "A dragon still starts as a drawing.", "graphite drawing pencil set"),
            s("Orchestral album", "The trailer's scale comes from an orchestra, which you can hear on its own.", "orchestral adventure album"),
        ),
    ),
    "big-buck-bunny" to listOf(
        cat(
            "garden",
            "Garden",
            s("Butterfly habitat", "The meadow is full of wings, which is a garden you can plant.", "butterfly garden kit"),
            s("Wildflower seed", "The bully-and-bunny story sits in a flower field.", "wildflower seed mix"),
            s("Peach preserves", "The fruit is the punchline and the pantry item.", "peach preserves"),
        ),
        cat(
            "picnic",
            "Picnic",
            s("Picnic blanket", "The scale of the rabbit makes a blanket the right piece of furniture.", "oversized picnic blanket"),
            s("Picnic basket", "A short this cheerful wants a meal on the grass.", "wicker picnic basket"),
            s("Fruit tree guide", "A peach tree is the long version of the joke.", "backyard fruit tree book"),
        ),
        cat(
            "outdoors",
            "Outside",
            s("Binoculars", "The forest is worth watching more slowly than the cartoon does.", "compact binoculars"),
            s("Trail camera", "The animals are the cast. A camera is how you meet the real ones.", "wildlife trail camera"),
            s("Nature journal", "A kid-sized notebook matches the film's brightness.", "kids nature journal"),
        ),
        cat(
            "make",
            "How it was made",
            s("Animation book", "An open movie is an invitation to see how cartoons are built.", "3d animation beginner book"),
            s("Open movie notes", "The Blender Foundation published the production, not just the film.", "open source filmmaking book"),
            s("Plush pattern", "A large rabbit is also a sewing project.", "plush toy sewing pattern"),
        ),
    ),
    "tears-of-steel" to listOf(
        cat(
            "robots",
            "Robots",
            s("Robot arm kit", "The machines in the short are the practical hobby, scaled down.", "desktop robot arm kit"),
            s("Arduino starter", "A film about rogue robots starts, in real life, on a bench.", "arduino starter kit"),
            s("Robotics book", "The fiction is nicer after a chapter on how arms actually move.", "introduction to robotics book"),
        ),
        cat(
            "city",
            "Amsterdam",
            s("City guide", "The short is also a portrait of a real city.", "Amsterdam travel guide"),
            s("Bicycle bell", "The street outside the fiction is a bike city.", "classic bicycle bell"),
            s("Camera strap", "A crew is filming inside the film, which makes the camera the prop.", "leather camera strap"),
        ),
        cat(
            "metal",
            "Metal",
            s("Metal sculpture kit", "Steel is in the title, and small sculpture is the safe way to meet it.", "sheet metal sculpture kit"),
            s("VFX book", "The project exists to show how effects are built in the open.", "visual effects filmmaking book"),
            s("Work jacket", "The crew dresses like people who move cases, not like astronauts.", "canvas work jacket"),
        ),
        cat(
            "sound",
            "Sound",
            s("Electronic album", "The score lives next to synths and machines.", "cinematic electronic album"),
            s("Studio headphones", "A effects short is also a sound edit.", "studio headphones"),
            s("Field recorder", "The city audio is half the realism.", "portable field recorder"),
        ),
    ),
    "elephants-dream" to listOf(
        cat(
            "puzzles",
            "Puzzles",
            s("Mechanical puzzle", "The world is a machine you are meant to keep turning.", "mechanical metal puzzle"),
            s("Clockwork kit", "Gears and housings are the scenery.", "clockwork model kit"),
            s("Gear set", "A handful of gears is the smallest piece of that place.", "toy gear building set"),
        ),
        cat(
            "books",
            "Books",
            s("Surrealism", "The rooms do not owe you a map, which is an old art argument.", "surrealism art book"),
            s("Industrial design", "The machine is designed, even when it feels dreamt.", "industrial design history book"),
            s("Open movie essay", "This was the first Blender open film, and the making is the lesson.", "open source animation book"),
        ),
        cat(
            "light",
            "Light",
            s("Brass task lamp", "The interiors are pools of warm light in a dark machine.", "brass task lamp"),
            s("Blueprint print", "A drawing of a machine is the honest poster for this film.", "architectural blueprint print"),
            s("Graphite set", "The spaces look drafted before they look painted.", "technical drawing pencil set"),
        ),
        cat(
            "sound",
            "Sound",
            s("Experimental album", "The score is closer to a machine humming than to a song.", "experimental electronic album"),
            s("Kinetic sculpture", "Something that moves when you are not touching it fits the halls.", "kinetic desk sculpture"),
            s("Notebook", "Two people disagree about what the machine means. Write your version.", "plain hardcover notebook"),
        ),
    ),
)

private fun cat(id: String, label: String, vararg suggestions: Suggestion) =
    Category(id, label, suggestions.toList())

private fun s(name: String, why: String, query: String) = Suggestion(name, why, query)
