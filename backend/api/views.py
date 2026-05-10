from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession

from django.contrib.auth.models import User
from django.contrib.auth import authenticate
import uuid
import datetime
from django.db.models import Sum
from datetime import date


TOKENS = {}

@csrf_exempt
def register(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Only POST'}, status=405)

    body = json.loads(request.body)

    username = body.get('username')
    password = body.get('password')

    if not username or not password:
        return JsonResponse({'error': 'Missing fields'}, status=400)

    if User.objects.filter(username=username).exists():
        return JsonResponse({'error': 'User exists'}, status=400)

    user = User.objects.create_user(username=username, password=password)

    return JsonResponse({'message': 'User created'})

@csrf_exempt
def login(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Only POST'}, status=405)

    body = json.loads(request.body)

    user = authenticate(
        username=body.get('username'),
        password=body.get('password')
    )

    if user is None:
        return JsonResponse({'error': 'Invalid credentials'}, status=401)

    token = str(uuid.uuid4())
    TOKENS[token] = user.id

    return JsonResponse({'token': token})

def get_user_from_token(request):
    token = request.headers.get('Authorization')

    if not token:
        return None

    user_id = TOKENS.get(token)
    if not user_id:
        return None

    return User.objects.get(id=user_id)

@csrf_exempt
def books(request):
    if request.method == 'GET':
        books = Book.objects.all()

        response = []
        for b in books:
            response.append({
                'id': b.id,
                'title': b.title,
                'author': {
                    'id': b.author.id,
                    'name': b.author.name
                },
                'total_pages': b.total_pages,
                'synopsis': b.synopsis,
                'genres': [g.name for g in b.genres.all()],
                'cover': b.cover if b.cover else None
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if not body.get('title'):
            return JsonResponse({'error': 'Missing title'}, status=400)

        if not body.get('author'):
            return JsonResponse({'error': 'Missing author'}, status=400)

        if not body.get('total_pages'):
            return JsonResponse({'error': 'Missing total_pages'}, status=400)

        if not body.get('cover'):
            return JsonResponse({'error': 'Missing cover'}, status=400)

        book = Book.objects.create(
            title=body['title'],
            author_id=body['author'],
            total_pages=body['total_pages'],
            synopsis=body.get('synopsis', ''),
            cover=body.get('cover', '')
        )

        if 'genres' in body:
            book.genres.set(body['genres'])

        return JsonResponse({'id': book.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def book_by_id(request, id):
    try:
        book = Book.objects.get(id=id)
    except Book.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': book.id,
            'title': book.title,
            'author': {
                'id': book.author.id,
                'name': book.author.name
            },
            'genres': [g.name for g in book.genres.all()],
            'cover': book.cover if book.cover else None
        })

    elif request.method == 'PUT':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if 'title' in body:
            book.title = body['title']

        if 'total_pages' in body:
            book.total_pages = body['total_pages']

        book.save()

        return JsonResponse({'updated': True})

    elif request.method == 'DELETE':
        book.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def annual_goals(request):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    if request.method == 'GET':

        annual_goals = AnnualGoal.objects.filter(user=user)

        response = []
        for a in annual_goals:
            response.append({
                'id': a.id,
                'year': a.year,
                'target_books': a.target_books,
                'user': a.user.id
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if not body.get('year'):
            return JsonResponse({'error': 'Missing year'}, status=400)

        if not body.get('target_books'):
            return JsonResponse({'error': 'Missing target_books'}, status=400)

        annual_goal = AnnualGoal.objects.create(
            user=user,
            year=body['year'],
            target_books=body['target_books']
        )

        return JsonResponse({'id': annual_goal.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def annual_goal_by_id(request, id):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    try:
        annual_goal = AnnualGoal.objects.get(id=id, user=user)
    except AnnualGoal.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': annual_goal.id,
            'year': annual_goal.year,
            'target_books': annual_goal.target_books
        })

    elif request.method == 'PUT':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if 'year' in body:
            annual_goal.year = body['year']

        if 'target_books' in body:
            annual_goal.target_books = body['target_books']

        annual_goal.save()

        return JsonResponse({'updated': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def authors(request):
    if request.method == 'GET':
        authors = Author.objects.all()

        response = []
        for a in authors:
            response.append({
                'id': a.id,
                'name': a.name,
            })

        return JsonResponse(response, safe=False)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def author_by_id(request, id):
    try:
        author = Author.objects.get(id=id)
    except Author.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': author.id,
            'name': author.name
        })

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def genres(request):
    if request.method == 'GET':
        genres = Genre.objects.all()

        response = []
        for g in genres:
            response.append({
                'id': g.id,
                'name': g.name,
            })

        return JsonResponse(response, safe=False)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def genre_by_id(request, id):
    try:
        genre = Genre.objects.get(id=id)
    except Genre.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': genre.id,
            'name': genre.name
        })

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def readings(request):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    if request.method == 'GET':

        readings = Reading.objects.filter(user=user)

        response = []
        for r in readings:
            response.append({
                'id': r.id,
                'book': {
                    'id': r.book.id,
                    'title': r.book.title,
                    'cover': r.book.cover,
                    'total_pages': r.book.total_pages,

                    'author': {
                        'id': r.book.author.id,
                        'name': r.book.author.name
                    }
                },
                'status': r.status,
                'start_date': r.start_date.isoformat() if r.start_date else None,
                'end_date': r.end_date.isoformat() if r.end_date else None
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if not body.get('book'):
            return JsonResponse({'error': 'Missing book'}, status=400)

        if not body.get('status'):
            return JsonResponse({'error': 'Missing status'}, status=400)

        reading, created = Reading.objects.get_or_create(
            user=user,
            book_id=body['book'],
            defaults={
                'status': body['status']
            }
        )

        if not created:
            reading.status = body['status']

            if body['status'] == 'completed':
                reading.end_date = datetime.date.today()

            reading.save()

        return JsonResponse({
            'id': reading.id,
            'created': created
        }, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_by_id(request, id):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    try:
        reading = Reading.objects.get(id=id, user=user)
    except Reading.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': reading.id,
            'book': {
                'id': reading.book.id,
                'title': reading.book.title
            },
            'status': reading.status
        })

    elif request.method == 'PUT':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if 'status' in body:
            reading.status = body['status']
        reading.save()

        return JsonResponse({'updated': True})

    elif request.method == 'DELETE':
        reading.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_sessions(request):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    if request.method == 'GET':
        sessions = ReadingSession.objects.filter(reading__user=user)

        response = []
        for s in sessions:
            response.append({
                'id': s.id,
                'reading': s.reading.id,
                'pages_read': s.pages_read,
                'minutes_read': s.minutes_read,
                'date': s.date.isoformat()
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if not body.get('reading'):
            return JsonResponse({'error': 'Missing reading'}, status=400)

        if not body.get('pages_read'):
            return JsonResponse({'error': 'Missing pages_read'}, status=400)

        if not body.get('minutes_read'):
            return JsonResponse({'error': 'Missing minutes_read'}, status=400)

        if not body.get('date'):
            return JsonResponse({'error': 'Missing date'}, status=400)

        try:
            reading = Reading.objects.get(id=body['reading'], user=user)
        except Reading.DoesNotExist:
            return JsonResponse({'error': 'Reading not found'}, status=404)

        session = ReadingSession.objects.create(
            reading=reading,
            pages_read=body.get('pages_read', 0),
            minutes_read=body.get('minutes_read', 0),
            date=body['date']
        )

        return JsonResponse({'id': session.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_session_by_id(request, id):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    try:
        session = ReadingSession.objects.get(id=id, reading__user=user)
    except ReadingSession.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': session.id,
            'reading': session.reading.id,
            'pages_read': session.pages_read,
            'minutes_read': session.minutes_read
        })

    elif request.method == 'DELETE':
        session.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def stats(request):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    current_year = date.today().year

    completed_readings = Reading.objects.filter(
        user=user,
        status='completed'
    )

    books_completed = completed_readings.count()

    pages_read = 0

    for r in completed_readings:
        if r.book:
            pages_read += r.book.total_pages

    goal = AnnualGoal.objects.filter(
        user=user,
        year=current_year
    ).first()

    target_books = goal.target_books if goal else 0

    progress = 0

    if target_books > 0:
        progress = int((books_completed / target_books) * 100)

    return JsonResponse({
        'books_completed': books_completed,
        'pages_read': pages_read,
        'target_books': target_books,
        'progress': progress
    })

@csrf_exempt
def me(request):

    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    return JsonResponse({
        'id': user.id,
        'username': user.username
    })

@csrf_exempt
def home(request):
    user = get_user_from_token(request)

    if not user:
        return JsonResponse({'error': 'Unauthorized'}, status=401)

    # 1. libro en lectura
    reading = Reading.objects.filter(user=user, status="reading").first()

    current_book = None
    if reading:
        current_book = {
            "title": reading.book.title,
            "total_pages": reading.book.total_pages,
        }

    # 2. objetivo anual
    from datetime import datetime
    year = datetime.now().year

    goal = AnnualGoal.objects.filter(user=user, year=year).first()

    target = goal.target_books if goal else 0

    # 3. libros completados
    books_read = Reading.objects.filter(user=user, status="completed").count()

    return JsonResponse({
        "current_book": current_book,
        "books_read": books_read,
        "goal": target
    })

