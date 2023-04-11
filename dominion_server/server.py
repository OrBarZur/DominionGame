from flask import Flask, request
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json

app = Flask(__name__)


'''
A function that creates a game with the function push that creates
a random and unique string as a key to the game and returns this key to the creator.
'''
@app.route('/creator_start', methods=['GET'])
def creator_start():
    game_id = get_games_ref().push().key

    return {**{"game_id": game_id}, **{"success": True}}


'''
A function that adds the second player to the table if there is only one player there.
'''
@app.route('/non_creator_start', methods=['POST'])
def non_creator_start():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    if game_id.child("before-game-data").child("idP2").get() != "":
        return {"success": False}
    game_id.child("before-game-data").update({"idP2": data.get("idP2")})

    return {"success": True}


'''
A function that uploads all data while game didn't start.
'''
@app.route('/upload_all_data', methods=['POST'])
def upload_all_data():
    data = request.json
    game_id = get_games_ref().child(data.get("gameManagerBeforeStart").get("gameId"))
    game_id.child("before-game-data").update({"idP1": data.get("gameManagerBeforeStart").get("idP1"),
                                              "idP2": data.get("gameManagerBeforeStart").get("idP2"),
                                              "isRated": data.get("gameManagerBeforeStart").get("isRated"),
                                              "isReady1": data.get("gameManagerBeforeStart").get("isReady1"),
                                              "isReady2": data.get("gameManagerBeforeStart").get("isReady2"),
                                              "isStart1": data.get("gameManagerBeforeStart").get("isStart1"),
                                              "isStart2": data.get("gameManagerBeforeStart").get("isStart2")
                                              })
    game_id.child("general-data").set({"actionCards": data.get("actionCards")})
    game_id.child("game-data").set({"board": json.loads(data.get("board")), "trash": data.get("trash"),
                                    "log": data.get("log"), "turn": data.get("turn"), "isGameEnded": data.get("isGameEnded")})

    return {"success": True}


'''
A function that uploads game data while game started.
'''
@app.route('/upload_real_time', methods=['POST'])
def upload_real_time():
    data = request.json
    game_id = get_games_ref().child(data.get("gameManagerBeforeStart").get("gameId"))
    game_id.child("game-data").update({"isGameEnded": data.get("isGameEnded")})
    if "board" in data and "trash" in data and "log" in data and "turn" in data:
        game_id.child("game-data").update({"board": json.loads(data.get("board")), "trash": data.get("trash"),
                                           "log": data.get("log")})
        game_id.child("game-data").child("turn").update(data.get("turn"))
        return {**data.get("turn"), **{"success": True}}

    data_response_game = game_id.child("game-data").get()
    data_response_game["board"] = json.dumps(data_response_game["board"])
    return {**data_response_game, **{"success": True}}


'''
A function that returns all data while game didn't start.
'''
@app.route('/get_all_data', methods=['POST'])
def get_all_data():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    data_response_before_game = game_id.child("before-game-data").get()
    data_response_game_general = game_id.child("general-data").get()
    data_response_game = game_id.child("game-data").get()

    if data_response_before_game is None or data_response_game_general is None or data_response_game is None:
        return {"success": False}

    data_response_game["board"] = json.dumps(data_response_game["board"])
    return {**data_response_before_game, **data_response_game_general, **data_response_game, **{"success": True}}


'''
A function that returns game data while game started.
'''
@app.route('/get_real_time', methods=['POST'])
def get_real_time():
    data = request.json
    if "gameManagerBeforeStart" not in data:
        game_id = get_games_ref().child(data.get("gameId"))
        data_response_game = game_id.child("game-data").get()
        if data_response_game is None:
            return {"success": False}

        data_response_game["board"] = json.dumps(data_response_game["board"])
        return {**data_response_game, **{"success": True}}

    game_id = get_games_ref().child(data.get("gameManagerBeforeStart").get("gameId"))
    game_id.child("game-data").update({"board": json.loads(data.get("board")), "trash": data.get("trash"), "log": data.get("log")})

    game_id.child("game-data").child("turn").update(data.get("turn"))
    print("get_real_time:" + " uploading data rn")
    return {"success": True}


'''
A function that uploads the data about the player that sent the request
and returns the data about the other player.
'''
@app.route('/get_and_upload_player_data', methods=['POST'])
def get_and_upload_player_data():
    data = request.json
    game_id = get_games_ref().child(data.get("gameManagerBeforeStart").get("gameId"))
    my_id = data.get("gameManagerBeforeStart").get("idP1")
    enemy_id = data.get("gameManagerBeforeStart").get("idP2")
    if not data.get("gameManagerBeforeStart").get("isCreator"):
        my_id, enemy_id = enemy_id, my_id
    game_id.child(my_id).set(data.get("myData"))

    enemy_data = game_id.child(enemy_id).get()
    if enemy_data is None:
        return {"success": False}

    return {**enemy_data, **{"success": True}}


'''
A function that returns before-game-data from the database.
'''
@app.route('/get_game_manager_before_start', methods=['POST'])
def get_game_manager_before_start():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    before_game_data = game_id.child("before-game-data").get()
    if before_game_data is None:
        return {"success": False}

    return {**before_game_data, **{"success": True}}


'''
A function that uploads before-game-data to the database.
'''
@app.route('/upload_game_manager_before_start', methods=['POST'])
def upload_game_manager_before_start():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.child("before-game-data").set({"idP1": data.get("idP1"),
                                           "idP2": data.get("idP2"),
                                           "isReady1": data.get("isReady1"),
                                           "isReady2": data.get("isReady2"),
                                           "isStart1": data.get("isStart1"),
                                           "isStart2": data.get("isStart2"),
                                           "isRated": data.get("isRated")})

    return {"success": True}


'''
A function that updates ready to the player sent the request.
'''
@app.route('/update_ready', methods=['POST'])
def update_ready():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.child("before-game-data").update({"isReady1": data.get("isReady1")}
                                             if data.get("isCreator")
                                             else {"isReady2": data.get("isReady2")})

    return {"success": True}


'''
A function that updates ready to start to the player sent the request.
'''
@app.route('/update_ready_to_start', methods=['POST'])
def update_ready_to_start():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.child("before-game-data").update({"isStart1": data.get("isStart1")}
                                             if data.get("isCreator")
                                             else {"isStart2": data.get("isStart2")})

    return {"success": True}


'''
A function that deletes the table.
'''
@app.route('/delete_game_manager_before_start', methods=['POST'])
def delete_game_manager_before_start():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.delete()

    return {"success": True}


'''
A function that deletes the second player.
'''
@app.route('/delete_p2', methods=['POST'])
def delete_p2():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.child("before-game-data").update({"idP2": "", "isReady2": False})

    return {"success": True}


'''
A function that returns all the tables in database.
'''
@app.route('/get_tables', methods=['GET'])
def get_tables():
    data_request = get_games_ref().get()
    if data_request is None:
        return {"success": False}
    data_edited = {}
    for game in data_request:
        if type(data_request.get(game)) != str:
            data_edited[game] = data_request.get(game).get("before-game-data")

    return {**data_edited, **{"success": True}}


'''
A function that deletes all the last game data to be ready for the next game.
'''
@app.route('/end_game', methods=['POST'])
def end_game():
    data = request.json
    game_id = get_games_ref().child(data.get("gameId"))
    game_id.child("game-data").delete()
    game_id.child("general-data").delete()
    game_id.child(data.get("idP1")).delete()
    game_id.child(data.get("idP2")).delete()
    game_id.child("before-game-data").update({"idP1": data.get("idP1"),
                                              "idP2": data.get("idP2"),
                                              "isReady1": False,
                                              "isReady2": False,
                                              "isStart1": False,
                                              "isStart2": False,
                                              "isRated": data.get("isRated")})

    return {"success": True}


'''
A function that gets the details that the player entered to register,
gets all users from database and checks if there is the username.
if the username exists, the function returns success false,
otherwise it creates the user and returns success true.
'''
@app.route('/register', methods=['POST'])
def register():
    data_register = request.json
    data_request = get_users_ref().get(shallow=True)
    if data_request is not None and data_register.get("username") in data_request.keys():
        return {"success": False}

    username = get_users_ref().child(data_register.get("username"))
    username.set({"email": data_register.get("email"), "password": data_register.get("password")})

    return {"username": data_register.get("username"), "success": True}


'''
A function that gets the details that the player entered to login,
gets all users from database and checks if there is the username.
if the username doesn't exist or the password doesn't match the username, the function returns success false,
otherwise it returns success true and returns the username.
'''
@app.route('/login', methods=['POST'])
def login():
    data_register = request.json
    data_request = get_users_ref().get(shallow=True)
    if data_request is None or data_register.get("username") not in data_request.keys()\
            or get_users_ref().child(data_register.get("username")).get().get("password") != data_register.get("password"):
        return {"success": False}

    return {"username": data_register.get("username"), "success": True}


def get_games_ref():
    return db.reference("Games")


def get_users_ref():
    return db.reference("Users")


if __name__ == '__main__':
    # initializes a new App instance and runs the flask
    firebase_admin.initialize_app(credentials.Certificate(
        r"C:\Users\or\Desktop\DominionGame\dominion-project-firebase-adminsdk-wzw45-052b5aeeea.json"),
        {'databaseURL': 'https://dominion-project.firebaseio.com'})
    app.run(host='0.0.0.0', port=8888)
