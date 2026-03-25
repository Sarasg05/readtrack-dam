from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json

from datetime import datetime

from django.contrib.auth.models import User
from django.contrib.auth.hashers import make_password
from django.contrib.auth import authenticate

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession


@csrf_exempt
def register(request):
    if request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        if not body.get('username'):
            return JsonResponse({'error': 'Missing username'}, status=400)

        if not body.get('password'):
            return JsonResponse({'error': 'Missing password'}, status=400)

        if User.objects.filter(username=body['username']).exists():
            return JsonResponse({'error': 'User already exists'}, status=400)

        user = User.objects.create(
            username=body['username'],
            password=make_password(body['password'])
        )

        return JsonResponse({'id': user.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def login(request):
    if request.method == 'POST':
        try:
            body = json.loads(request.body)
        except:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)

        user = authenticate(
            username=body.get('username'),
            password=body.get('password')
        )

        if user is None:
            return JsonResponse({'error': 'Invalid credentials'}, status=401)

        return JsonResponse({
            'message': 'Login successful',
            'user_id': user.id
        })

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

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
                'genres': [g.name for g in b.genres.all()]
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

        book = Book.objects.create(
            title=body['title'],
            author_id=body['author'],
            total_pages=body['total_pages'],
            synopsis=body.get('synopsis', '')
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
            'author': book.author.name,
            'genres': [g.name for g in book.genres.all()]
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
    if request.method == 'GET':
        user_id = request.GET.get('user', None)

        annual_goals = AnnualGoal.objects.all()
        if user_id:
            annual_goals = annual_goals.filter(user_id=user_id)

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

        if not body.get('user'):
            return JsonResponse({'error': 'Missing user'}, status=400)

        if not body.get('year'):
            return JsonResponse({'error': 'Missing year'}, status=400)

        if not body.get('target_books'):
            return JsonResponse({'error': 'Missing target_books'}, status=400)

        annual_goal = AnnualGoal.objects.create(
            user_id=body['user'],
            year=body['year'],
            target_books=body['target_books']
        )

        return JsonResponse({'id': annual_goal.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def annual_goal_by_id(request, id):
    try:
        annual_goal = AnnualGoal.objects.get(id=id)
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
    if request.method == 'GET':
        user_id = request.GET.get('user', None)

        readings = Reading.objects.all()
        if user_id:
            readings = readings.filter(user_id=user_id)

        response = []
        for r in readings:
            response.append({
                'id': r.id,
                'book': r.book.title,
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

        if not body.get('user'):
            return JsonResponse({'error': 'Missing user'}, status=400)

        if not body.get('book'):
            return JsonResponse({'error': 'Missing book'}, status=400)

        if not body.get('status'):
            return JsonResponse({'error': 'Missing status'}, status=400)

        reading = Reading.objects.create(

            user_id=body['user'],

            book_id=body['book'],

            status=body['status']

        )

        return JsonResponse({'id': reading.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_by_id(request, id):
    try:
        reading = Reading.objects.get(id=id)
    except Reading.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': reading.id,
            'book': reading.book.title,
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
    if request.method == 'GET':
        sessions = ReadingSession.objects.all()

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

        date = datetime.fromisoformat(body['date']).date()

        session = ReadingSession.objects.create(
            reading_id=body['reading'],
            pages_read=body['pages_read'],
            minutes_read=body['minutes_read'],
            date=date
        )

        return JsonResponse({'id': session.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_session_by_id(request, id):
    try:
        session = ReadingSession.objects.get(id=id)
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

